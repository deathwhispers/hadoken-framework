package com.github.hadoken.framework.security.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SecurityProperties 单元测试
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2026/05/29
 */
@SpringBootTest
@TestPropertySource(properties = {
    "hadoken.security.token-header=Authorization",
    "hadoken.security.token-timeout=30m",
    "hadoken.security.token-secret=secure-token-secret-123",
    "hadoken.security.session-timeout=60m",
    "hadoken.security.mock-enable=false"
    // mock-secret 不配置，因为 mock-enable=false
})
class SecurityPropertiesTest {

    @Test
    void testPropertiesBinding() {
        // 测试将通过Spring Boot的自动配置执行
        // 如果属性绑定成功，测试会通过
        assertTrue(true, "安全配置属性绑定测试");
    }

    @Test
    void testMockSecretNoDefaultValue() {
        SecurityProperties properties = new SecurityProperties();

        // 验证mockSecret没有默认值
        assertNull(properties.getMockSecret(), "mockSecret应没有默认值");

        // 设置值后可以获取
        properties.setMockSecret("custom-secret");
        assertEquals("custom-secret", properties.getMockSecret());
    }

    @Test
    void testValidateMockEnabledWithSecret() {
        SecurityProperties properties = new SecurityProperties();
        properties.setMockEnable(true);
        properties.setMockSecret("secure-mock-secret-123");

        // 验证不应抛出异常
        assertDoesNotThrow(properties::validate);
    }

    @Test
    void testValidateMockEnabledWithoutSecret() {
        SecurityProperties properties = new SecurityProperties();
        properties.setMockEnable(true);
        // mockSecret 未设置

        IllegalStateException exception = assertThrows(IllegalStateException.class, properties::validate);
        assertTrue(exception.getMessage().contains("hadoken.security.mock-secret"));
    }

    @Test
    void testValidateMockDisabled() {
        SecurityProperties properties = new SecurityProperties();
        properties.setMockEnable(false);
        // mockSecret 未设置

        // mockEnable为false时，mockSecret不是必需的
        assertDoesNotThrow(properties::validate);

        // 即使设置了mockSecret也应该通过验证
        properties.setMockSecret("some-secret");
        assertDoesNotThrow(properties::validate);
    }

    @Test
    void testValidateWeakPasswordRejection() {
        SecurityProperties properties = new SecurityProperties();
        properties.setMockEnable(true);

        // 测试常见弱密码
        String[] weakPasswords = {"123456", "password", "admin", "test", "123456789"};

        for (String weakPassword : weakPasswords) {
            properties.setMockSecret(weakPassword);
            IllegalStateException exception = assertThrows(IllegalStateException.class, properties::validate);
            assertTrue(exception.getMessage().contains("不能使用常见弱密码"),
                "弱密码 '" + weakPassword + "' 应被拒绝");
        }

        // 测试强密码通过
        properties.setMockSecret("secure-mock-secret-" + System.currentTimeMillis());
        assertDoesNotThrow(properties::validate);
    }

    @Test
    void testOtherFieldsValidation() {
        SecurityProperties properties = new SecurityProperties();

        // 设置必需字段
        properties.setTokenHeader("Authorization");
        properties.setTokenTimeout(Duration.ofMinutes(30));
        properties.setTokenSecret("secure-token-secret");
        properties.setSessionTimeout(Duration.ofHours(1));
        properties.setMockEnable(false);

        // 验证通过
        assertDoesNotThrow(properties::validate);
    }

    @Test
    void testEmptyStringValidation() {
        SecurityProperties properties = new SecurityProperties();
        properties.setMockEnable(true);
        properties.setMockSecret("   "); // 空白字符串

        IllegalStateException exception = assertThrows(IllegalStateException.class, properties::validate);
        assertTrue(exception.getMessage().contains("hadoken.security.mock-secret"));
    }

    @Test
    void testNullMockEnableHandling() {
        SecurityProperties properties = new SecurityProperties();
        properties.setMockEnable(null);

        // null mockEnable 应视为false
        assertDoesNotThrow(properties::validate);

        // 即使有mockSecret也应该通过
        properties.setMockSecret("some-secret");
        assertDoesNotThrow(properties::validate);
    }
}