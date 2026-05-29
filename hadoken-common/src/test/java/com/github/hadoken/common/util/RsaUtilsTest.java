package com.github.hadoken.common.util;

import org.junit.Test;

import java.util.Base64;

import static org.junit.Assert.*;

/**
 * RsaUtils 单元测试
 * <p>
 * 测试RSA密钥升级和Base64依赖替换
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2026/05/29
 */
public class RsaUtilsTest {

    private String testPlaintext = "RSA测试文本123!@#";

    @Test
    public void testRsaKeyLengthUpgrade() throws Exception {
        // 测试密钥长度从1024升级到2048
        RsaUtils.RsaKeyPair keyPair = RsaUtils.generateKeyPair();

        assertNotNull("生成的密钥对应非空", keyPair);
        assertNotNull("公钥非空", keyPair.getPublicKey());
        assertNotNull("私钥非空", keyPair.getPrivateKey());

        // 验证密钥长度（通过Base64解码后分析）
        byte[] publicKeyBytes = Base64.getDecoder().decode(keyPair.getPublicKey());
        assertTrue("2048位RSA公钥长度应至少256字节", publicKeyBytes.length >= 256);

        byte[] privateKeyBytes = Base64.getDecoder().decode(keyPair.getPrivateKey());
        assertTrue("2048位RSA私钥长度应至少256字节", privateKeyBytes.length >= 256);
    }

    @Test
    public void testRsaEncryptionDecryption() throws Exception {
        // 生成测试密钥对
        RsaUtils.RsaKeyPair keyPair = RsaUtils.generateKeyPair();

        // 公钥加密
        String encrypted = RsaUtils.encryptByPublicKey(keyPair.getPublicKey(), testPlaintext);
        assertNotNull("加密结果不应为null", encrypted);
        assertNotEquals("加密结果应与原文不同", testPlaintext, encrypted);

        // 私钥解密
        String decrypted = RsaUtils.decryptByPrivateKey(keyPair.getPrivateKey(), encrypted);
        assertEquals("解密结果应与原文相同", testPlaintext, decrypted);
    }

    @Test
    public void testRsaPrivateEncryptionPublicDecryption() throws Exception {
        // 测试私钥加密、公钥解密
        RsaUtils.RsaKeyPair keyPair = RsaUtils.generateKeyPair();

        // 私钥加密
        String encrypted = RsaUtils.encryptByPrivateKey(keyPair.getPrivateKey(), testPlaintext);
        assertNotNull("加密结果不应为null", encrypted);

        // 公钥解密
        String decrypted = RsaUtils.decryptByPublicKey(keyPair.getPublicKey(), encrypted);
        assertEquals("解密结果应与原文相同", testPlaintext, decrypted);
    }

    @Test
    public void testRsaKeyPairStructure() {
        // 测试RsaKeyPair内部类结构
        RsaUtils.RsaKeyPair keyPair = new RsaUtils.RsaKeyPair("test-public", "test-private");

        assertEquals("test-public", keyPair.getPublicKey());
        assertEquals("test-private", keyPair.getPrivateKey());
    }

    @Test
    public void testRsaInvalidKeyHandling() {
        // 测试无效密钥处理
        try {
            RsaUtils.encryptByPublicKey("invalid-base64-key", testPlaintext);
            fail("无效密钥加密应抛出异常");
        } catch (Exception e) {
            // 预期异常
        }

        try {
            RsaUtils.decryptByPrivateKey("invalid-base64-key", "encrypted-data");
            fail("无效密钥解密应抛出异常");
        } catch (Exception e) {
            // 预期异常
        }
    }

    @Test
    public void testRsaEmptyInputHandling() throws Exception {
        // 测试空输入
        RsaUtils.RsaKeyPair keyPair = RsaUtils.generateKeyPair();

        // 空文本加密
        try {
            RsaUtils.encryptByPublicKey(keyPair.getPublicKey(), null);
            fail("null文本加密应抛出异常");
        } catch (Exception e) {
            // 预期异常
        }

        try {
            RsaUtils.encryptByPublicKey(keyPair.getPublicKey(), "");
            fail("空文本加密应抛出异常");
        } catch (Exception e) {
            // 预期异常
        }

        // 空密钥
        try {
            RsaUtils.encryptByPublicKey("", testPlaintext);
            fail("空密钥加密应抛出异常");
        } catch (Exception e) {
            // 预期异常
        }

        try {
            RsaUtils.encryptByPublicKey(null, testPlaintext);
            fail("null密钥加密应抛出异常");
        } catch (Exception e) {
            // 预期异常
        }
    }

    @Test
    public void testRsaKeyPairGenerationWithCustomSize() throws Exception {
        // 测试自定义密钥长度生成
        RsaUtils.RsaKeyPair keyPair = RsaUtils.generateKeyPair(2048);
        assertNotNull(keyPair);

        // 测试最小密钥长度
        try {
            RsaUtils.generateKeyPair(1024);
            fail("小于2048位的密钥长度应抛出异常");
        } catch (IllegalArgumentException e) {
            assertTrue("错误信息应包含密钥长度", e.getMessage().contains("2048"));
        }

        // 测试更大的密钥长度
        RsaUtils.RsaKeyPair largeKeyPair = RsaUtils.generateKeyPair(3072);
        assertNotNull(largeKeyPair);
    }
}