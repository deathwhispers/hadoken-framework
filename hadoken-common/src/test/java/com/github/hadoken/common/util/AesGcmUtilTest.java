package com.github.hadoken.common.util;

import org.junit.Before;
import org.junit.Test;

import java.util.Base64;

import static org.junit.Assert.*;

/**
 * AesGcmUtil 单元测试
 * <p>
 * 测试AES-256-GCM加密功能，遵循TDD的RED阶段
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2026/05/29
 */
public class AesGcmUtilTest {

    private byte[] validKeyBytes;
    private byte[] invalidKeyBytes;
    private byte[] wrongKeyBytes;

    @Before
    public void setUp() {
        // 32字节有效AES-256密钥（Base64编码）
        String base64Key = "dGhpcy1pcy1hLXRlc3QtYWVzLWtleS0zMi1ieXRlcw==";
        validKeyBytes = Base64.getDecoder().decode(base64Key);

        // 8字节无效密钥
        invalidKeyBytes = Base64.getDecoder().decode("dGVzdC1rZXk=");

        // 另一个32字节错误密钥
        wrongKeyBytes = Base64.getDecoder().decode("YW5vdGhlci1hZXMta2V5LWZvci10ZXN0aW5n");
    }

    @Test
    public void testAesEncryptionSuccess() throws Exception {
        // RED阶段：这个测试应该失败，因为AesGcmUtil还不存在
        // 测试1: AES-256-GCM加密能成功加密文本
        String plaintext = "这是一段测试文本，包含中文和英文123!@#";
        String encrypted = AesGcmUtil.encrypt(plaintext, validKeyBytes);

        assertNotNull("加密结果不应为null", encrypted);
        assertNotEquals("加密结果应与原文不同", plaintext, encrypted);
        assertTrue("加密结果不应为空字符串", encrypted.length() > 0);
    }

    @Test
    public void testAesDecryptionSuccess() throws Exception {
        // RED阶段：这个测试应该失败，因为AesGcmUtil还不存在
        // 测试2: AES-256-GCM解密能还原原始文本
        String plaintext = "测试解密功能";
        String encrypted = AesGcmUtil.encrypt(plaintext, validKeyBytes);

        String decrypted = AesGcmUtil.decrypt(encrypted, validKeyBytes);

        assertNotNull("解密结果不应为null", decrypted);
        assertEquals("解密结果应与原文相同", plaintext, decrypted);
    }

    @Test
    public void testAesEncryptionRandomIv() throws Exception {
        // RED阶段：这个测试应该失败，因为AesGcmUtil还不存在
        // 测试3: 使用不同密钥加密相同文本得到不同结果（由于随机IV）
        String plaintext = "相同明文测试";

        String encrypted1 = AesGcmUtil.encrypt(plaintext, validKeyBytes);
        String encrypted2 = AesGcmUtil.encrypt(plaintext, validKeyBytes);

        assertNotEquals("由于随机IV，相同明文加密应得到不同结果", encrypted1, encrypted2);

        // 但都能正确解密
        assertEquals(plaintext, AesGcmUtil.decrypt(encrypted1, validKeyBytes));
        assertEquals(plaintext, AesGcmUtil.decrypt(encrypted2, validKeyBytes));
    }

    @Test
    public void testAesDecryptWithWrongKey() {
        // RED阶段：这个测试应该失败，因为AesGcmUtil还不存在
        // 测试4: 使用错误密钥解密抛出异常
        String plaintext = "测试错误密钥";

        try {
            String encrypted = AesGcmUtil.encrypt(plaintext, validKeyBytes);

            // 使用错误密钥解密应失败
            AesGcmUtil.decrypt(encrypted, wrongKeyBytes);
            fail("使用错误密钥解密应该抛出异常");
        } catch (Exception e) {
            // 预期行为
            assertNotNull("异常不应为null", e);
        }
    }

    @Test
    public void testAesEncryptInvalidKeyLength() {
        // RED阶段：这个测试应该失败，因为AesGcmUtil还不存在
        // 测试5: 无效密钥长度应抛出异常
        try {
            AesGcmUtil.encrypt("测试文本", invalidKeyBytes);
            fail("无效密钥长度应抛出异常");
        } catch (Exception e) {
            // 预期行为
            assertNotNull("异常不应为null", e);
        }
    }

    @Test
    public void testDecodeKeyFromBase64() {
        // RED阶段：这个测试应该失败，因为AesGcmUtil还不存在
        // 测试密钥解码方法
        String base64Key = "dGhpcy1pcy1hLXRlc3QtYWVzLWtleS0zMi1ieXRlcw==";
        byte[] decodedKey = AesGcmUtil.decodeKeyFromBase64(base64Key);

        assertNotNull("解码后的密钥不应为null", decodedKey);
        assertEquals("密钥长度应为32字节", 32, decodedKey.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecodeKeyFromBase64Null() {
        // RED阶段：这个测试应该失败，因为AesGcmUtil还不存在
        // 测试空密钥解码
        AesGcmUtil.decodeKeyFromBase64(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDecodeKeyFromBase64Empty() {
        // RED阶段：这个测试应该失败，因为AesGcmUtil还不存在
        // 测试空字符串密钥解码
        AesGcmUtil.decodeKeyFromBase64("");
    }

    @Test
    public void testIsValidAes256Key() {
        // RED阶段：这个测试应该失败，因为AesGcmUtil还不存在
        // 测试密钥验证方法
        assertTrue("32字节密钥应有效", AesGcmUtil.isValidAes256Key(validKeyBytes));
        assertFalse("8字节密钥应无效", AesGcmUtil.isValidAes256Key(invalidKeyBytes));
        assertFalse("null密钥应无效", AesGcmUtil.isValidAes256Key(null));
    }

    @Test
    public void testGenerateRandomKey() {
        // RED阶段：这个测试应该失败，因为AesGcmUtil还不存在
        // 测试随机密钥生成
        String randomKey = AesGcmUtil.generateRandomKey();
        assertNotNull("生成的随机密钥不应为null", randomKey);

        // 验证生成的密钥可以被解码并验证
        byte[] decodedKey = Base64.getDecoder().decode(randomKey);
        assertEquals("生成的随机密钥应为32字节", 32, decodedKey.length);
    }
}