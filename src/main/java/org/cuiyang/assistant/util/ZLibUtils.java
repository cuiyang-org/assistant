package org.cuiyang.assistant.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ZLibUtils {
    public static byte[] compress(byte[] bArr) {
        Deflater deflater = new Deflater();
        deflater.setLevel(9);
        deflater.setInput(bArr);
        deflater.finish();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bArr.length);
        byte[] bArr2 = new byte[1024];
        while (!deflater.finished()) {
            byteArrayOutputStream.write(bArr2, 0, deflater.deflate(bArr2));
        }
        deflater.end();
        try {
            byteArrayOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] decompress(byte[] bArr) {
        Inflater inflater = new Inflater();
        inflater.setInput(bArr, 0, bArr.length);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bArr.length);
        byte[] bArr2 = new byte[4096];
        while (!inflater.finished()) {
            try {
                byteArrayOutputStream.write(bArr2, 0, inflater.inflate(bArr2));
            } catch (DataFormatException e) {
                e.printStackTrace();
            }
        }
        inflater.end();
        try {
            byteArrayOutputStream.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return byteArrayOutputStream.toByteArray();
    }
}
