package com.github.hadoken.framework.upgrade;

import org.junit.Test;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.SpringVersion;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class SpringBootVersionTest {

    @Test
    public void testSpringBootVersion() {
        String version = SpringBootVersion.getVersion();
        assertNotNull("Spring Boot version should not be null", version);
        System.out.println("Spring Boot Version: " + version);

        // 验证版本为 3.5.x
        assertTrue("Spring Boot version should be 3.5.x, got: " + version,
            version.startsWith("3.5."));
    }

    @Test
    public void testSpringFrameworkVersion() {
        String version = SpringVersion.getVersion();
        assertNotNull("Spring Framework version should not be null", version);
        System.out.println("Spring Framework Version: " + version);

        // Spring Boot 3.5.x 应该使用 Spring Framework 6.2.x
        assertTrue("Spring Framework version should be 6.2.x for Spring Boot 3.5.x, got: " + version,
            version.startsWith("6.2.") || version.startsWith("6.3."));
    }
}