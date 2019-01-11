package org.cuiyang.assistant.util;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Base64;

/**
 * DES加密工具类
 *
 * @author cy48576
 */
public class DesUtils {

    public static String DEFAULT_TRANSFORMATION = "DES/CBC/PKCS5Padding";

    private DesUtils() {
    }

    private static Cipher getCipher(int mode, byte[] key, byte[] iv) throws Exception {
        // 创建一个DESKeySpec对象
        DESKeySpec desKeySpec = new DESKeySpec(key);
        // 创建一个密匙工厂，然后用它把DESKeySpec转换成secureKey
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(DEFAULT_TRANSFORMATION);
        // 用密匙初始化Cipher对象
        if (iv != null) {
            cipher.init(mode, secretKey, new IvParameterSpec(iv));
        } else {
            cipher.init(mode, secretKey);
        }
        return cipher;
    }

    /**
     * 加密
     */
    public static byte[] encrypt(byte[] data, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(data);
    }

    /**
     * 加密
     */
    public static String encryptHex(byte[] data, byte[] key, byte[] iv) throws Exception {
        byte[] decrypt = decrypt(data, key, iv);
        return Hex.encodeHexString(decrypt);
    }

    /**
     * 加密
     */
    public static String encryptBase64(byte[] data, byte[] key, byte[] iv) throws Exception {
        byte[] decrypt = decrypt(data, key, iv);
        return Base64.getEncoder().encodeToString(decrypt);
    }

    /**
     * 解密
     */
    public static byte[] decrypt(byte[] data, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(data);
    }

}
