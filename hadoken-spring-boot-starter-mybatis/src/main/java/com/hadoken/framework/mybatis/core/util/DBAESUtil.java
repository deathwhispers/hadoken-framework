package com.hadoken.framework.mybatis.core.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

/**
 * 加解密工具类
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/8/25 10:19
 */
public class DBAESUtil {
    private static final String DEFAULT_V = "6859505890402435";

    private static final String KEY = "rhy";
    private static final String ALGORITHM = "AES";

    private static SecretKeySpec getKey() {
        byte[] arrBTmp = DBAESUtil.KEY.getBytes();

        // 创建一个空的16位字节数组（默认值为0）
        byte[] arrB = new byte[16];
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }
        return new SecretKeySpec(arrB, ALGORITHM);
    }

    /**
     * 加密
     */
    public static String encrypt(String content) throws Exception {
        final Base64.Encoder encoder = Base64.getEncoder();
        SecretKeySpec keySpec = getKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(DEFAULT_V.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
        byte[] encrypted = cipher.doFinal(content.getBytes());
        return encoder.encodeToString(encrypted);
    }


    /**
     * 解密
     */
    public static String decrypt(String content) throws Exception {
        final Base64.Decoder decoder = Base64.getDecoder();
        SecretKeySpec keySpec = getKey();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(DEFAULT_V.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
        byte[] base64 = decoder.decode(content);
        byte[] original = cipher.doFinal(base64);
        return new String(original);
    }
}
