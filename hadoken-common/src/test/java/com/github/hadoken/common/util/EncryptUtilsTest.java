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
 * EncryptUtils 单元测试
 * <p>
 * 测试AES-256-GCM加密功能
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2026/05/29
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = com.github.hadoken.common.TestApplication.class)
@TestPropertySource(properties = {
    "hadoken.security.encrypt.aes-key=dGhpcy1pcy1hLXRlc3QtYWVzLWtleS0zMi1ieXRlcw==", // 32字节Base64
    "hadoken.security.encrypt.rsa-private-key=test-rsa-private-key",
    "hadoken.security.encrypt.rsa-public-key=test-rsa-public-key",
    "hadoken.security.encrypt.enabled=true"
})
public class EncryptUtilsTest {

    @Autowired
    private EncryptProperties encryptProperties;

    @Before
    public void setUp() {
        // 确保加密功能启用
        encryptProperties.setEnabled(true);
    }

    @Test
    public void testAesEncryptionDecryption() throws Exception {
        // 准备测试数据
        String plaintext = "这是一段测试文本，包含中文和英文123!@#";

        // 加密
        String encrypted = EncryptUtils.desEncrypt(plaintext);
        assertNotNull("加密结果不应为null", encrypted);
        assertNotEquals("加密结果应与原文不同", plaintext, encrypted);
        assertTrue("加密结果不应为空字符串", encrypted.length() > 0);

        // 解密
        String decrypted = EncryptUtils.desDecrypt(encrypted);
        assertNotNull("解密结果不应为null", decrypted);
        assertEquals("解密结果应与原文相同", plaintext, decrypted);
    }

    @Test
    public void testAesEncryptionRandomIv() throws Exception {
        // 测试随机IV：相同明文加密两次得到不同结果
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
        // 测试已弃用的DES方法抛出异常
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
        // 测试工具方法仍然可用
        byte[] original = "测试数据".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        String hex = EncryptUtils.byte2hex(original);
        assertNotNull(hex);
        assertTrue(hex.length() > 0);

        byte[] converted = EncryptUtils.hex2byte(hex.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        assertArrayEquals("hex2byte应能还原byte2hex的结果", original, converted);
    }

    @Test
    public void testByte2hexNullInput() {
        // 测试null输入
        String result = EncryptUtils.byte2hex(null);
        assertEquals("null输入应返回空字符串", "", result);
    }

    @Test
    public void testByte2hexEmptyArray() {
        // 测试空数组输入
        String result = EncryptUtils.byte2hex(new byte[0]);
        assertEquals("空数组输入应返回空字符串", "", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHex2byteInvalidLength() {
        // 测试无效长度输入
        byte[] invalid = "123".getBytes(java.nio.charset.StandardCharsets.UTF_8); // 长度不是偶数
        EncryptUtils.hex2byte(invalid);
    }

    @Test
    public void testAesDecryptWithWrongKey() {
        // 使用错误密钥解密应失败
        String plaintext = "测试错误密钥";

        try {
            String encrypted = EncryptUtils.desEncrypt(plaintext);

            // 修改加密数据的第一个字节（模拟错误密钥）
            byte[] decoded = java.util.Base64.getDecoder().decode(encrypted);
            decoded[0] = (byte) (decoded[0] ^ 0xFF); // 修改IV的第一个字节
            String tampered = java.util.Base64.getEncoder().encodeToString(decoded);

            // 解密应失败
            try {
                EncryptUtils.desDecrypt(tampered);
                fail("使用错误密钥解密应失败");
            } catch (Exception e) {
                // 预期行为
            }
        } catch (Exception e) {
            fail("加密过程不应失败: " + e.getMessage());
        }
    }

    @Test
    public void testAesEncryptNullInput() {
        // 空输入测试
        try {
            EncryptUtils.desEncrypt(null);
            fail("加密null文本应抛出异常");
        } catch (Exception e) {
            // 预期行为
            assertNotNull("异常不应为null", e);
            assertTrue("应抛出IllegalArgumentException", e instanceof IllegalArgumentException);
        }

        try {
            EncryptUtils.desEncrypt("");
            fail("加密空文本应抛出异常");
        } catch (Exception e) {
            // 预期行为
            assertNotNull("异常不应为null", e);
            assertTrue("应抛出IllegalArgumentException", e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testAesDecryptNullInput() {
        // 空输入测试
        try {
            EncryptUtils.desDecrypt(null);
            fail("解密null文本应抛出异常");
        } catch (Exception e) {
            // 预期行为
            assertNotNull("异常不应为null", e);
            assertTrue("应抛出IllegalArgumentException", e instanceof IllegalArgumentException);
        }

        try {
            EncryptUtils.desDecrypt("");
            fail("解密空文本应抛出异常");
        } catch (Exception e) {
            // 预期行为
            assertNotNull("异常不应为null", e);
            assertTrue("应抛出IllegalArgumentException", e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testAesEncryptDisabled() {
        // 禁用加密功能测试
        encryptProperties.setEnabled(false);

        try {
            EncryptUtils.desEncrypt("测试文本");
            fail("加密功能禁用时应抛出异常");
        } catch (Exception e) {
            // 预期行为
            assertNotNull("异常不应为null", e);
            assertTrue("应抛出IllegalStateException", e instanceof IllegalStateException);
        }

        try {
            EncryptUtils.desDecrypt("加密数据");
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
    public void testAesKeyValidation() {
        // 测试无效密钥长度
        EncryptProperties invalidProps = new EncryptProperties();
        invalidProps.setAesKey("dGVzdC1rZXk="); // 只有8字节，不是32字节
        invalidProps.setEnabled(true);

        // 直接测试AesGcmUtil的密钥验证
        byte[] invalidKey = java.util.Base64.getDecoder().decode("dGVzdC1rZXk=");
        assertFalse("8字节密钥应无效", AesGcmUtil.isValidAes256Key(invalidKey));

        byte[] validKey = java.util.Base64.getDecoder().decode("dGhpcy1pcy1hLXRlc3QtYWVzLWtleS0zMi1ieXRlcw==");
        assertTrue("32字节密钥应有效", AesGcmUtil.isValidAes256Key(validKey));
    }
}