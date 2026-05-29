package com.github.hadoken.framework.upgrade;

import org.junit.Test;

import jakarta.servlet.http.HttpServlet;
import jakarta.ws.rs.core.Application;

import static org.junit.Assert.assertTrue;

/**
 * Jakarta EE 版本验证测试
 * 验证 Jakarta EE API 版本与 Spring Boot 3.5.x 兼容
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2026/05/29
 */
public class JakartaEEVersionTest {

    @Test
    public void testJakartaServletVersion() {
        // 检查 Jakarta Servlet API 版本
        Package servletPackage = HttpServlet.class.getPackage();
        String specificationVersion = servletPackage.getSpecificationVersion();

        assertTrue("Jakarta Servlet API specification should be 5.0 or higher for Spring Boot 3.5.x, got: " + specificationVersion,
            specificationVersion != null &&
            (specificationVersion.startsWith("5.") ||
             specificationVersion.startsWith("6.")));

        System.out.println("Jakarta Servlet API Version: " + specificationVersion);
    }

    @Test
    public void testJakartaRSVersion() {
        // 检查 Jakarta RESTful Web Services API 版本
        Package rsPackage = Application.class.getPackage();
        String specificationVersion = rsPackage.getSpecificationVersion();

        assertTrue("Jakarta RESTful Web Services API specification should be 3.1 or higher for Spring Boot 3.5.x, got: " + specificationVersion,
            specificationVersion != null &&
            (specificationVersion.startsWith("3.")));

        System.out.println("Jakarta RESTful Web Services API Version: " + specificationVersion);
    }
}
