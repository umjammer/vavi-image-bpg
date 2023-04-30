/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.imageio.bpg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import vavi.image.bpg.BPG;
import vavi.util.Debug;


/**
 * BpgImageReaderSpi.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2023-04-30 umjammer initial version <br>
 */
public class BpgImageReaderSpi extends ImageReaderSpi {

    private static final String VendorName = "https://github.com/umjammer/vavi-image-avif";
    private static final String Version = "0.0.1";
    private static final String ReaderClassName =
        "vavi.imageio.bpg.BpgImageReader";
    private static final String[] Names = {
        "bpg", "AVIF"
    };
    private static final String[] Suffixes = {
        "bpg"
    };
    private static final String[] mimeTypes = {
        "image/bpg"
    };
    static final String[] WriterSpiNames = {};
    private static final boolean SupportsStandardStreamMetadataFormat = false;
    private static final String NativeStreamMetadataFormatName = null;
    private static final String NativeStreamMetadataFormatClassName = null;
    private static final String[] ExtraStreamMetadataFormatNames = null;
    private static final String[] ExtraStreamMetadataFormatClassNames = null;
    private static final boolean SupportsStandardImageMetadataFormat = false;
    private static final String NativeImageMetadataFormatName = "bpg";
    private static final String NativeImageMetadataFormatClassName = null;
    private static final String[] ExtraImageMetadataFormatNames = null;
    private static final String[] ExtraImageMetadataFormatClassNames = null;

    /** */
    public BpgImageReaderSpi() {
        super(VendorName,
              Version,
              Names,
              Suffixes,
              mimeTypes,
              ReaderClassName,
              new Class[] { ImageInputStream.class },
              WriterSpiNames,
              SupportsStandardStreamMetadataFormat,
              NativeStreamMetadataFormatName,
              NativeStreamMetadataFormatClassName,
              ExtraStreamMetadataFormatNames,
              ExtraStreamMetadataFormatClassNames,
              SupportsStandardImageMetadataFormat,
              NativeImageMetadataFormatName,
              NativeImageMetadataFormatClassName,
              ExtraImageMetadataFormatNames,
              ExtraImageMetadataFormatClassNames);
    }

    @Override
    public String getDescription(Locale locale) {
        return "BPG Image";
    }

    @Override
    public boolean canDecodeInput(Object obj) throws IOException {
Debug.println(Level.FINE, "input: " + obj);
        if (obj instanceof ImageInputStream) {
            ImageInputStream stream = (ImageInputStream) obj;
            stream.mark();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] b = new byte[8192];
            while (true) {
                int r = stream.read(b, 0, b.length);
                if (r < 0) break;
                baos.write(b, 0, r);
            }
            int l = baos.size();
Debug.println(Level.FINE, "size: " + l);
            stream.reset();
            BPG bpg = new BPG();
            return bpg.isBpgImage(baos.toByteArray());
        } else {
            return false;
        }
    }

    @Override
    public ImageReader createReaderInstance(Object obj) {
        return new BpgImageReader(this);
    }
}
