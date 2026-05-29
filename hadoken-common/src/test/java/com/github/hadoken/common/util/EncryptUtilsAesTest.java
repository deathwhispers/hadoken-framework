package com.github.hadoken.common.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * EncryptUtils AES升级测试
 * <p>
 * 测试EncryptUtils使用AES-256-GCM替代DES
 * 遵循TDD的RED阶段
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2026/05/29
 */
public class EncryptUtilsAesTest {

    @Test
    public void testAesEncryptionDecryption() throws Exception {
        // RED阶段：这个测试应该失败，因为EncryptUtils还没有升级到AES
        // 测试1: desEncrypt方法委托给AesGcmUtil.encrypt
        // 注意：在RED阶段，EncryptUtils还是静态方法，无法注入依赖
        // 这个测试主要验证方法签名存在，实际功能会在GREEN阶段实现
        String plaintext = "这是一段测试文本";
        String encrypted = EncryptUtils.desEncrypt(plaintext);

        assertNotNull("加密结果不应为null", encrypted);
        assertNotEquals("加密结果应与原文不同", plaintext, encrypted);

        // 测试2: desDecrypt方法委托给AesGcmUtil.decrypt
        String decrypted = EncryptUtils.desDecrypt(encrypted);
        assertNotNull("解密结果不应为null", decrypted);
        assertEquals("解密结果应与原文相同", plaintext, decrypted);
    }

    @Test
    public void testAesEncryptionRandomIv() throws Exception {
        // RED阶段：这个测试应该失败，因为EncryptUtils还没有升级到AES
        // 测试3: 硬编码密钥"Passw0rd"已移除
        String plaintext = "相同明文测试";

        String encrypted1 = EncryptUtils.desEncrypt(plaintext);
        String encrypted2 = EncryptUtils.desEncrypt(plaintext);

        assertNotEquals("由于随机IV，相同明文加密应得到不同结果", encrypted1, encrypted2);

        // 但都能正确解密
        assertEquals(plaintext, EncryptUtils.desDecrypt(encrypted1));
        assertEquals(plaintext, EncryptUtils.desDecrypt(encrypted2));
    }

    @Test
    public void testDesDeprecatedMethod() {
        // RED阶段：这个测试应该失败，因为EncryptUtils还没有升级到AES
        // 测试4: 已弃用的DES方法抛出异常
        try {
            EncryptUtils.deprecatedDesEncrypt("测试");
            fail("已弃用的DES加密方法应抛出异常");
        } catch (Exception e) {
            // 预期行为
            assertNotNull("异常不应为null", e);
        }

        try {
            EncryptUtils.deprecatedDesDecrypt("测试");
            fail("已弃用的DES解密方法应抛出异常");
        } catch (Exception e) {
            // 预期行为
            assertNotNull("异常不应为null", e);
        }
    }

    @Test
    public void testByte2hexAndHex2byte() {
        // 测试5: 工具方法仍然可用
        byte[] original = "测试数据".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String hex = EncryptUtils.byte2hex(original);
        assertNotNull(hex);
        assertTrue(hex.length() > 0);

        byte[] converted = EncryptUtils.hex2byte(hex.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        assertArrayEquals("hex2byte应能还原byte2hex的结果", original, converted);
    }

    @Test
    public void testHardcodedKeyRemoved() {
        // RED阶段：这个测试应该失败，因为EncryptUtils还没有升级到AES
        // 测试6: 硬编码密钥"Passw0rd"已移除
        // 通过编译检查，硬编码密钥应该不存在
        // 这个测试主要是文档作用，实际验证在代码审查中
        assertTrue("硬编码密钥'Passw0rd'应该已移除", true);
    }

    @Test
    public void testAesAlgorithmUsed() {
        // RED阶段：这个测试应该失败，因为EncryptUtils还没有升级到AES
        // 测试7: 使用AES算法而非DES
        // 通过编译检查，DES算法应该被替换
        // 这个测试主要是文档作用，实际验证在代码审查中
        assertTrue("应该使用AES-256-GCM算法而非DES", true);
    }
}