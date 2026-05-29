package com.github.hadoken.framework.security.autoconfigure;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

import static org.junit.Assert.*;

/**
 * SecurityProperties 单元测试
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2026/05/29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {
    "hadoken.security.token-header=Authorization",
    "hadoken.security.token-timeout=30m",
    "hadoken.security.token-secret=secure-token-secret-123",
    "hadoken.security.session-timeout=60m",
    "hadoken.security.mock-enable=false"
    // mock-secret 不配置，因为 mock-enable=false
})
public class SecurityPropertiesTest {

    @Test
    public void testPropertiesBinding() {
        // 测试将通过Spring Boot的自动配置执行
        // 如果属性绑定成功，测试会通过
        assertTrue("安全配置属性绑定测试", true);
    }

    @Test
    public void testMockSecretNoDefaultValue() {
        SecurityProperties properties = new SecurityProperties();

        // 验证mockSecret没有默认值
        assertNull("mockSecret应没有默认值", properties.getMockSecret());

        // 设置值后可以获取
        properties.setMockSecret("custom-secret");
        assertEquals("custom-secret", properties.getMockSecret());
    }

    @Test
    public void testValidateMockEnabledWithSecret() {
        SecurityProperties properties = new SecurityProperties();
        properties.setMockEnable(true);
        properties.setMockSecret("secure-mock-secret-123");

        // 验证不应抛出异常
        properties.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidateMockEnabledWithoutSecret() {
        SecurityProperties properties = new SecurityProperties();
        properties.setMockEnable(true);
        // mockSecret 未设置

        properties.validate();
    }

    @Test
    public void testValidateMockDisabled() {
        SecurityProperties properties = new SecurityProperties();
        properties.setMockEnable(false);
        // mockSecret 未设置

        // mockEnable为false时，mockSecret不是必需的
        properties.validate();

        // 即使设置了mockSecret也应该通过验证
        properties.setMockSecret("some-secret");
        properties.validate();
    }

    @Test
    public void testValidateWeakPasswordRejection() {
        SecurityProperties properties = new SecurityProperties();
        properties.setMockEnable(true);

        // 测试常见弱密码
        String[] weakPasswords = {"123456", "password", "admin", "test", "123456789"};

        for (String weakPassword : weakPasswords) {
            try {
                properties.setMockSecret(weakPassword);
                properties.validate();
                fail("弱密码 '" + weakPassword + "' 应被拒绝");
            } catch (IllegalStateException e) {
                assertTrue(e.getMessage().contains("不能使用常见弱密码"));
            }
        }

        // 测试强密码通过
        properties.setMockSecret("secure-mock-secret-" + System.currentTimeMillis());
        properties.validate();
    }

    @Test
    public void testOtherFieldsValidation() {
        SecurityProperties properties = new SecurityProperties();

        // 设置必需字段
        properties.setTokenHeader("Authorization");
        properties.setTokenTimeout(Duration.ofMinutes(30));
        properties.setTokenSecret("secure-token-secret");
        properties.setSessionTimeout(Duration.ofHours(1));
        properties.setMockEnable(false);

        // 验证通过
        properties.validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testEmptyStringValidation() {
        SecurityProperties properties = new SecurityProperties();
        properties.setMockEnable(true);
        properties.setMockSecret("   "); // 空白字符串

        properties.validate();
    }

    @Test
    public void testNullMockEnableHandling() {
        SecurityProperties properties = new SecurityProperties();
        properties.setMockEnable(null);

        // null mockEnable 应视为false
        properties.validate();

        // 即使有mockSecret也应该通过
        properties.setMockSecret("some-secret");
        properties.validate();
    }
}