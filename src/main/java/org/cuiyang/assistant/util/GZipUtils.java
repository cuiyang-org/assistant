package org.cuiyang.assistant.util;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GZipUtils {

    public static byte[] compress(byte[] bArr) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
        if (bArr != null) {
            gzipOutputStream.write(bArr);
        }
        gzipOutputStream.close();
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        return byteArray;
    }

    public static byte[] uncompress(byte[] bArr) throws IOException {
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bArr));
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            IOUtils.copy(gzipInputStream, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }

}
