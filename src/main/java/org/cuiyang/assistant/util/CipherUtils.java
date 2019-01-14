package org.cuiyang.assistant.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 加密工具类
 *
 * @author cy48576
 */
public class CipherUtils {

    public static String DEFAULT_CHARSET = "UTF-8";
    public static String DEFAULT_ALGORITHM = "AES";
    public static String DEFAULT_TRANSFORMATION = "AES/ECB/PKCS5Padding";

    private CipherUtils() {
    }

    /**
     * 加密
     */
    public static byte[] encrypt(byte[] data, byte[] key, byte[] iv) throws Exception {
        if (data == null || key == null) {
            return null;
        }
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(data);
    }

    /**
     * 加密
     */
    public static byte[] encrypt(String data, String key, String iv) throws Exception {
        return encrypt(getByte(data), getByte(key), getByte(iv));
    }

    /**
     * 加密
     */
    public static String encryptHex(byte[] data, byte[] key, byte[] iv) throws Exception {
        byte[] encrypt = encrypt(data, key, iv);
        return Hex.encodeHexString(encrypt);
    }

    /**
     * 加密
     */
    public static String encryptHex(String data, String key, String iv) throws Exception {
        return encryptHex(getByte(data), getByte(key), getByte(iv));
    }

    /**
     * 加密
     */
    public static String encryptBase64(byte[] data, byte[] key, byte[] iv) throws Exception {
        byte[] encrypt = encrypt(data, key, iv);
        return Base64.getEncoder().encodeToString(encrypt);
    }

    /**
     * 加密
     */
    public static String encryptBase64(String data, String key, String iv) throws Exception {
        return encryptBase64(getByte(data), getByte(key), getByte(iv));
    }

    /**
     * 解密
     */
    public static byte[] decrypt(byte[] data, byte[] key, byte[] iv) throws Exception {
        if (data == null || key == null) {
            return null;
        }
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(data);
    }

    /**
     * 解密
     */
    public static String decryptHex(String data, String key, String iv) throws Exception {
        if (data == null || data.isEmpty()) {
            return null;
        }
        byte[] decrypt = decrypt(Hex.decodeHex(data.toCharArray()), getByte(key), getByte(iv));
        return new String(decrypt, DEFAULT_CHARSET);
    }

    /**
     * 解密
     */
    public static String decryptBase64(String data, String key, String iv) throws Exception {
        if (data == null || data.isEmpty()) {
            return null;
        }
        byte[] decrypt = decrypt(Base64.getDecoder().decode(data), getByte(key), getByte(iv));
        return new String(decrypt, DEFAULT_CHARSET);
    }

    private static Cipher getCipher(int mode, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance(DEFAULT_TRANSFORMATION);
        if (iv != null) {
            cipher.init(mode, new SecretKeySpec(key, DEFAULT_ALGORITHM), new IvParameterSpec(iv));
        } else {
            cipher.init(mode, new SecretKeySpec(key, DEFAULT_ALGORITHM));
        }
        return cipher;
    }

    private static byte[] getByte(String str) throws Exception {
        if (str == null || str.isEmpty()) {
            return null;
        } else {
            return str.getBytes(DEFAULT_CHARSET);
        }
    }
}
