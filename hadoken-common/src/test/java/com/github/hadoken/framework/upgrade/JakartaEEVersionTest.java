package com.github.hadoken.framework.upgrade;

import org.junit.Test;

import jakarta.servlet.http.HttpServlet;

import static org.junit.Assert.assertTrue;

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
}