/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.image.bpg;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.util.logging.Level;

import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import vavi.awt.image.jna.libbpg.BPGImageInfo;
import vavi.util.Debug;

import static vavi.awt.image.jna.libbpg.LibbpgLibrary.BPGDecoderOutputFormat.BPG_OUTPUT_FORMAT_RGB24;
import static vavi.awt.image.jna.libbpg.LibbpgLibrary.BPGDecoderOutputFormat.BPG_OUTPUT_FORMAT_RGBA32;
import static vavi.awt.image.jna.libbpg.LibbpgLibrary.INSTANCE;


/**
 * BPG.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2023-04-30 nsano initial version <br>
 */
public class BPG {

    /** native library */
    private final PointerByReference decoder;

    /* creates decoder instance */
    public BPG() {
        decoder = INSTANCE.bpg_decoder_open();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
Debug.println(Level.FINE, "shutdownHook");
            INSTANCE.bpg_decoder_close(decoder);
        }));
    }

    /** decode a buffer */
    public BufferedImage decode(byte[] bpg) {

        int r = INSTANCE.bpg_decoder_decode(decoder, bpg, bpg.length);
        if (r < 0) {
            throw new IllegalStateException("bpg_decoder_decode: " + r);
        }

        BPGImageInfo info = new BPGImageInfo();
        INSTANCE.bpg_decoder_get_info(decoder, info);

        int w = info.width;
        int h = info.height;
        boolean a = info.has_alpha != 0;

        BufferedImage image = new BufferedImage(w, h, a ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR);
        byte[] d = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        ByteBuffer m = ByteBuffer.allocateDirect(3 * w);

        r = INSTANCE.bpg_decoder_start(decoder, a ? BPG_OUTPUT_FORMAT_RGBA32 : BPG_OUTPUT_FORMAT_RGB24);
        if (r < 0) {
            throw new IllegalStateException("bpg_decoder_start: " + r);
        }

        for (int y = 0; y < h; y++) {
            r = INSTANCE.bpg_decoder_get_line(decoder, Native.getDirectBufferPointer(m));
            if (r < 0) {
                throw new IllegalStateException("bpg_decoder_get_line: " + r);
            }

            int b = (a ? 4 : 3) * w * y;
            int p = 0;
            for (int x = 0; x < w; x++) {
                if (a) {
                    d[b + p + 3] = m.get(p + 2);
                    d[b + p + 2] = m.get(p + 1);
                    d[b + p + 1] = m.get(p + 0);
                    d[b + p + 0] = m.get(p + 3);
                    p += 4;
                } else {
                    d[b + p + 2] = m.get(p + 0);
                    d[b + p + 1] = m.get(p + 1);
                    d[b + p + 0] = m.get(p + 2);
                    p += 3;
                }
            }
        }

        return image;
    }

    /** check if a buffer decodeable */
    public boolean isBpgImage(byte[] bpg) {
        return INSTANCE.bpg_decoder_decode(decoder, bpg, bpg.length) == 0;
    }
}
