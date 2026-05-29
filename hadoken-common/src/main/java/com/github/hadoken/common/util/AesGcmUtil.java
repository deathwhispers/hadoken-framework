package com.github.hadoken.common.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

/**
 * AES-256-GCM 加密工具类
 * <p>
 * 使用AES-256-GCM算法进行对称加密，提供认证加密功能。
 * 每个加密操作使用随机IV（12字节），IV与密文一起存储。
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2026/05/29
 */
public class AesGcmUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128; // GCM认证标签长度
    private static final int IV_LENGTH_BYTE = 12;  // 推荐IV长度
    private static final int AES_256_KEY_LENGTH_BYTE = 32; // AES-256密钥长度（32字节）

    /**
     * AES-256-GCM加密
     *
     * @param plaintext 明文文本
     * @param keyBytes AES-256密钥字节数组（必须为32字节）
     * @return Base64编码的密文（包含IV）
     * @throws Exception 加密失败时抛出异常
     */
    public static String encrypt(String plaintext, byte[] keyBytes) throws Exception {
        // 验证密钥长度
        if (keyBytes == null || keyBytes.length != AES_256_KEY_LENGTH_BYTE) {
            throw new IllegalArgumentException(
                String.format("AES-256密钥必须为%d字节，实际为%s",
                    AES_256_KEY_LENGTH_BYTE,
                    keyBytes == null ? "null" : keyBytes.length + "字节")
            );
        }

        // 生成随机IV
        byte[] iv = new byte[IV_LENGTH_BYTE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        // 创建密钥规范
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        // 初始化加密器
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec);

        // 执行加密
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        // 组合IV和密文：IV + 密文
        byte[] encrypted = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, encrypted, 0, iv.length);
        System.arraycopy(ciphertext, 0, encrypted, iv.length, ciphertext.length);

        // Base64编码返回
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * AES-256-GCM解密
     *
     * @param ciphertext Base64编码的密文（包含IV）
     * @param keyBytes AES-256密钥字节数组（必须为32字节）
     * @return 解密后的明文文本
     * @throws Exception 解密失败时抛出异常
     */
    public static String decrypt(String ciphertext, byte[] keyBytes) throws Exception {
        // 验证密钥长度
        if (keyBytes == null || keyBytes.length != AES_256_KEY_LENGTH_BYTE) {
            throw new IllegalArgumentException(
                String.format("AES-256密钥必须为%d字节，实际为%s",
                    AES_256_KEY_LENGTH_BYTE,
                    keyBytes == null ? "null" : keyBytes.length + "字节")
            );
        }

        // Base64解码
        byte[] decoded = Base64.getDecoder().decode(ciphertext);

        // 验证数据长度至少包含IV
        if (decoded.length < IV_LENGTH_BYTE) {
            throw new IllegalArgumentException("密文长度无效，必须包含IV和加密数据");
        }

        // 提取IV和实际密文
        byte[] iv = Arrays.copyOfRange(decoded, 0, IV_LENGTH_BYTE);
        byte[] encrypted = Arrays.copyOfRange(decoded, IV_LENGTH_BYTE, decoded.length);

        // 创建密钥规范
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        // 初始化解密器
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);

        // 执行解密
        byte[] plaintext = cipher.doFinal(encrypted);

        return new String(plaintext, StandardCharsets.UTF_8);
    }

    /**
     * 从Base64字符串解码密钥字节数组
     *
     * @param base64Key Base64编码的密钥字符串
     * @return 解码后的密钥字节数组
     */
    public static byte[] decodeKeyFromBase64(String base64Key) {
        if (base64Key == null || base64Key.trim().isEmpty()) {
            throw new IllegalArgumentException("密钥不能为空");
        }
        return Base64.getDecoder().decode(base64Key.trim());
    }

    /**
     * 验证密钥是否为有效的AES-256密钥（32字节）
     *
     * @param keyBytes 密钥字节数组
     * @return 如果密钥有效返回true
     */
    public static boolean isValidAes256Key(byte[] keyBytes) {
        return keyBytes != null && keyBytes.length == AES_256_KEY_LENGTH_BYTE;
    }

    /**
     * 生成随机AES-256密钥（用于测试或密钥轮换）
     *
     * @return Base64编码的随机AES-256密钥
     */
    public static String generateRandomKey() {
        byte[] key = new byte[AES_256_KEY_LENGTH_BYTE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
}