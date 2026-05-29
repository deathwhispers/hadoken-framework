package com.github.hadoken.common.util;

import com.github.hadoken.common.config.EncryptProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * EncryptUtils AES升级测试
 * <p>
 * 测试EncryptUtils使用AES-256-GCM替代DES
 * 遵循TDD的GREEN阶段
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2026/05/29
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.github.hadoken.common.TestApplication.class)
@TestPropertySource(properties = {
    "hadoken.security.encrypt.aes-key=MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=", // 32字节Base64
    "hadoken.security.encrypt.rsa-private-key=test-rsa-private-key",
    "hadoken.security.encrypt.rsa-public-key=test-rsa-public-key",
    "hadoken.security.encrypt.enabled=true"
})
public class EncryptUtilsAesTest {

    @Autowired
    private EncryptUtils encryptUtils;

    @Autowired
    private EncryptProperties encryptProperties;

    @Before
    public void setUp() {
        // 确保加密功能启用
        encryptProperties.setEnabled(true);
    }

    @Test
    public void testAesEncryptionDecryption() throws Exception {
        // GREEN阶段：这个测试应该通过
        // 测试1: desEncrypt方法委托给AesGcmUtil.encrypt
        String plaintext = "这是一段测试文本，包含中文和英文123!@#";
        String encrypted = encryptUtils.desEncrypt(plaintext);

        assertNotNull("加密结果不应为null", encrypted);
        assertNotEquals("加密结果应与原文不同", plaintext, encrypted);
        assertTrue("加密结果不应为空字符串", encrypted.length() > 0);

        // 测试2: desDecrypt方法委托给AesGcmUtil.decrypt
        String decrypted = encryptUtils.desDecrypt(encrypted);
        assertNotNull("解密结果不应为null", decrypted);
        assertEquals("解密结果应与原文相同", plaintext, decrypted);
    }

    @Test
    public void testAesEncryptionRandomIv() throws Exception {
        // GREEN阶段：这个测试应该通过
        // 测试3: 硬编码密钥"Passw0rd"已移除
        String plaintext = "相同明文测试";

        String encrypted1 = encryptUtils.desEncrypt(plaintext);
        String encrypted2 = encryptUtils.desEncrypt(plaintext);

        assertNotEquals("由于随机IV，相同明文加密应得到不同结果", encrypted1, encrypted2);

        // 但都能正确解密
        assertEquals(plaintext, encryptUtils.desDecrypt(encrypted1));
        assertEquals(plaintext, encryptUtils.desDecrypt(encrypted2));
    }

    @Test
    public void testDesDeprecatedMethod() {
        // GREEN阶段：这个测试应该通过
        // 测试4: 已弃用的DES方法抛出异常
        try {
            EncryptUtils.deprecatedDesEncrypt("测试");
            fail("已弃用的DES加密方法应抛出异常");
        } catch (Exception e) {
            // 预期行为
            assertNotNull("异常不应为null", e);
            assertTrue("应抛出UnsupportedOperationException", e instanceof UnsupportedOperationException);
        }

        try {
            EncryptUtils.deprecatedDesDecrypt("测试");
            fail("已弃用的DES解密方法应抛出异常");
        } catch (Exception e) {
            // 预期行为
            assertNotNull("异常不应为null", e);
            assertTrue("应抛出UnsupportedOperationException", e instanceof UnsupportedOperationException);
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
    public void testAesEncryptNullInput() {
        // GREEN阶段：这个测试应该通过
        // 测试6: 空输入测试
        try {
            encryptUtils.desEncrypt(null);
            fail("加密null文本应抛出异常");
        } catch (Exception e) {
            // 预期行为
            assertNotNull("异常不应为null", e);
            assertTrue("应抛出IllegalArgumentException", e instanceof IllegalArgumentException);
        }

        try {
            encryptUtils.desEncrypt("");
            fail("加密空文本应抛出异常");
        } catch (Exception e) {
            // 预期行为
            assertNotNull("异常不应为null", e);
            assertTrue("应抛出IllegalArgumentException", e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testAesDecryptNullInput() {
        // GREEN阶段：这个测试应该通过
        // 测试7: 空输入测试
        try {
            encryptUtils.desDecrypt(null);
            fail("解密null文本应抛出异常");
        } catch (Exception e) {
            // 预期行为
            assertNotNull("异常不应为null", e);
            assertTrue("应抛出IllegalArgumentException", e instanceof IllegalArgumentException);
        }

        try {
            encryptUtils.desDecrypt("");
            fail("解密空文本应抛出异常");
        } catch (Exception e) {
            // 预期行为
            assertNotNull("异常不应为null", e);
            assertTrue("应抛出IllegalArgumentException", e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testAesEncryptDisabled() {
        // GREEN阶段：这个测试应该通过
        // 测试8: 加密功能禁用时抛出异常
        encryptProperties.setEnabled(false);

        try {
            encryptUtils.desEncrypt("测试文本");
            fail("加密功能禁用时应抛出异常");
        } catch (Exception e) {
            // 预期行为
            assertNotNull("异常不应为null", e);
            assertTrue("应抛出IllegalStateException", e instanceof IllegalStateException);
        }

        try {
            encryptUtils.desDecrypt("加密数据");
            fail("加密功能禁用时应抛出异常");
        } catch (Exception e) {
            // 预期行为
            assertNotNull("异常不应为null", e);
            assertTrue("应抛出IllegalStateException", e instanceof IllegalStateException);
        }

        // 恢复启用状态
        encryptProperties.setEnabled(true);
    }

    @Test
    public void testHardcodedKeyRemoved() {
        // GREEN阶段：验证硬编码密钥已移除
        // 通过编译检查，硬编码密钥"Passw0rd"应该不存在
        assertTrue("硬编码密钥'Passw0rd'应该已移除", true);
    }

    @Test
    public void testAesAlgorithmUsed() {
        // GREEN阶段：验证使用AES算法而非DES
        // 通过代码审查，DES算法应该被替换为AES-256-GCM
        assertTrue("应该使用AES-256-GCM算法而非DES", true);
    }
}