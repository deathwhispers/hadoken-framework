package com.github.hadoken.common.config;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * EncryptProperties 单元测试
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2026/05/29
 */
public class EncryptPropertiesTest {

    @Test
    public void testValidateSuccess() {
        EncryptProperties properties = new EncryptProperties();
        properties.setAesKey("test-aes-key");
        properties.setRsaPrivateKey("test-rsa-private-key");
        properties.setRsaPublicKey("test-rsa-public-key");
        properties.setEnabled(true);

        // 验证不应抛出异常
        try {
            properties.validate();
        } catch (Exception e) {
            fail("验证成功时不应抛出异常: " + e.getMessage());
        }
    }

    @Test
    public void testValidateMissingAesKey() {
        EncryptProperties properties = new EncryptProperties();
        properties.setRsaPrivateKey("test-rsa-private-key");
        properties.setRsaPublicKey("test-rsa-public-key");
        properties.setEnabled(true);

        try {
            properties.validate();
            fail("缺少AES密钥时应抛出异常");
        } catch (IllegalStateException e) {
            assertTrue("错误信息应包含aes-key", e.getMessage().contains("hadoken.security.encrypt.aes-key"));
        }
    }

    @Test
    public void testValidateMissingRsaPrivateKey() {
        EncryptProperties properties = new EncryptProperties();
        properties.setAesKey("test-aes-key");
        properties.setRsaPublicKey("test-rsa-public-key");
        properties.setEnabled(true);

        try {
            properties.validate();
            fail("缺少RSA私钥时应抛出异常");
        } catch (IllegalStateException e) {
            assertTrue("错误信息应包含rsa-private-key", e.getMessage().contains("hadoken.security.encrypt.rsa-private-key"));
        }
    }

    @Test
    public void testValidateMissingRsaPublicKey() {
        EncryptProperties properties = new EncryptProperties();
        properties.setAesKey("test-aes-key");
        properties.setRsaPrivateKey("test-rsa-private-key");
        properties.setEnabled(true);

        try {
            properties.validate();
            fail("缺少RSA公钥时应抛出异常");
        } catch (IllegalStateException e) {
            assertTrue("错误信息应包含rsa-public-key", e.getMessage().contains("hadoken.security.encrypt.rsa-public-key"));
        }
    }

    @Test
    public void testValidateDisabled() {
        EncryptProperties properties = new EncryptProperties();
        properties.setEnabled(false);

        // 禁用时即使缺少密钥也不应抛出异常
        try {
            properties.validate();
        } catch (Exception e) {
            fail("禁用时验证不应抛出异常: " + e.getMessage());
        }
    }

    @Test
    public void testDefaultValues() {
        EncryptProperties properties = new EncryptProperties();
        assertEquals("默认应启用加密", true, properties.isEnabled());
    }
}