/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import vavi.awt.image.jna.libbpg.BPGImageInfo;
import vavi.awt.image.jna.libbpg.LibbpgLibrary;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * TestBpg.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-10-03 nsano initial version <br>
 */
@PropsEntity(url = "file:local.properties")
public class TestBpg {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "file")
    String file = "src/test/resources/squirrel.bpg";

    @BeforeEach
    void setup() throws IOException {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }
    }

    /**
     * @param args 0: bpg
     */
    public static void main(String[] args) throws Exception {
        TestBpg app = new TestBpg();
        app.setup();
        app.test();
    }

    @Test
    @DisplayName("prototype")
    @EnabledIfSystemProperty(named = "vavi.test", matches = "ide")
    void test() throws Exception {
        byte[] bpg = Files.readAllBytes(Paths.get(file));

        PointerByReference decoder = LibbpgLibrary.INSTANCE.bpg_decoder_open();
        int r = LibbpgLibrary.INSTANCE.bpg_decoder_decode(decoder, bpg, bpg.length);
        if (r < 0) {
            throw new IOException("bpg_decoder_decode: " + r);
        }

        BPGImageInfo info = new BPGImageInfo();
        LibbpgLibrary.INSTANCE.bpg_decoder_get_info(decoder, info);

        int w = info.width;
        int h = info.height;

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        byte[] d = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        ByteBuffer m = ByteBuffer.allocateDirect(3 * w);

        LibbpgLibrary.INSTANCE.bpg_decoder_start(decoder, LibbpgLibrary.BPGDecoderOutputFormat.BPG_OUTPUT_FORMAT_RGB24);
        for (int y = 0; y < h; y++) {
            LibbpgLibrary.INSTANCE.bpg_decoder_get_line(decoder, Native.getDirectBufferPointer(m));

            int b = 3 * w * y;
            int p = 0;
            for (int x = 0; x < w; x++) {
                d[b + p + 2] = m.get(p + 0);
                d[b + p + 1] = m.get(p + 1);
                d[b + p + 0] = m.get(p + 2);
                p += 3;
            }
        }

        LibbpgLibrary.INSTANCE.bpg_decoder_close(decoder);

        show(image);
    }

    /** using cdl cause junit stops awt thread suddenly */
    private void show(BufferedImage image) throws Exception {
        CountDownLatch cdl = new CountDownLatch(1);
        JFrame frame = new JFrame();
        frame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { cdl.countDown(); }
        });
        JPanel panel = new JPanel() {
            @Override public void paintComponent(Graphics g) {
                g.drawImage(image, 0, 0, this);
            }
        };
        panel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
        frame.setContentPane(new JScrollPane(panel));
        frame.setTitle("BPG");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        cdl.await();
    }

    @Test
    @DisplayName("spi specified")
    void test02() throws Exception {
        ImageReader ir = ImageIO.getImageReadersByFormatName("bpg").next();
        ImageInputStream iis = ImageIO.createImageInputStream(Files.newInputStream(Paths.get(file)));
        ir.setInput(iis);
        BufferedImage image = ir.read(0);
        assertNotNull(image);
    }

    @Test
    @DisplayName("spi auto")
    void test03() throws Exception {
        BufferedImage image = ImageIO.read(new File(file));
        assertNotNull(image);
    }

    @Test
    @DisplayName("spi specified gui")
    @EnabledIfSystemProperty(named = "vavi.test", matches = "ide")
    void test2() throws Exception {
long t = System.currentTimeMillis();
        ImageReader ir = ImageIO.getImageReadersByFormatName("bpg").next();
        ImageInputStream iis = ImageIO.createImageInputStream(Files.newInputStream(Paths.get(file)));
        ir.setInput(iis);
        BufferedImage image = ir.read(0);
Debug.println((System.currentTimeMillis() - t) + " ms");

        show(image);
    }

    @Test
    @DisplayName("spi auto gui")
    @EnabledIfSystemProperty(named = "vavi.test", matches = "ide")
    void test3() throws Exception {
long t = System.currentTimeMillis();
        BufferedImage image = ImageIO.read(new File(file));
Debug.println((System.currentTimeMillis() - t) + " ms");

        show(image);
    }
}
