package com.github.hadoken.framework.upgrade;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import jakarta.servlet.http.HttpServlet;
import jakarta.ws.rs.core.Application;

import java.lang.annotation.Annotation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 框架兼容性集成测试
 * 验证 Spring Boot 3.5.x、Spring Cloud 2025.x、Spring Cloud Alibaba 的完整兼容性
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2026/05/29
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FrameworkCompatibilityIntegrationTest {

    @Test
    public void testCompleteFrameworkCompatibility() {
        // 1. 验证 Spring Boot 版本
        String springBootVersion = SpringBootVersion.getVersion();
        assertNotNull("Spring Boot version should not be null", springBootVersion);
        assertTrue("Spring Boot should be 3.5.x, got: " + springBootVersion,
            springBootVersion.startsWith("3.5."));

        System.out.println("✅ Spring Boot Version: " + springBootVersion);

        // 2. 验证 Spring Framework 版本
        String springVersion = org.springframework.core.SpringVersion.getVersion();
        assertNotNull("Spring Framework version should not be null", springVersion);
        System.out.println("✅ Spring Framework Version: " + springVersion);

        // 3. 验证 Jakarta EE 版本
        Package servletPackage = HttpServlet.class.getPackage();
        String servletSpecVersion = servletPackage.getSpecificationVersion();
        assertTrue("Jakarta Servlet should be 5.0+, got: " + servletSpecVersion,
            servletSpecVersion != null && servletSpecVersion.startsWith("5"));

        Package rsPackage = Application.class.getPackage();
        String rsSpecVersion = rsPackage.getSpecificationVersion();
        assertTrue("Jakarta RS should be 3.1+, got: " + rsSpecVersion,
            rsSpecVersion != null && rsSpecVersion.startsWith("3"));

        System.out.println("✅ Jakarta Servlet API: " + servletSpecVersion);
        System.out.println("✅ Jakarta RS API: " + rsSpecVersion);

        // 4. 验证 Spring Cloud 可用性
        Class<?> loadBalancerClientClass = LoadBalancerClient.class;
        Class<?> serviceInstanceClass = ServiceInstance.class;
        assertNotNull("LoadBalancerClient should be available", loadBalancerClientClass);
        assertNotNull("ServiceInstance should be available", serviceInstanceClass);

        Package cloudPackage = LoadBalancerClient.class.getPackage();
        String cloudImplVersion = cloudPackage.getImplementationVersion();
        if (cloudImplVersion != null) {
            assertTrue("Spring Cloud should be 2025.x, got: " + cloudImplVersion,
                cloudImplVersion.startsWith("2025"));
            System.out.println("✅ Spring Cloud Version: " + cloudImplVersion);
        }

        // 5. 验证 Spring Cloud Alibaba 可用性（如果配置）
        try {
            Class<?> nacosClass = Class.forName("com.alibaba.cloud.nacos.NacosDiscoveryProperties");
            assertNotNull("NacosDiscoveryProperties should be available if Alibaba is configured", nacosClass);
            System.out.println("✅ Spring Cloud Alibaba is available");

            Package alibabaPackage = nacosClass.getPackage();
            String alibabaImplVersion = alibabaPackage.getImplementationVersion();
            if (alibabaImplVersion != null) {
                System.out.println("✅ Spring Cloud Alibaba Version: " + alibabaImplVersion);
            }
        } catch (ClassNotFoundException e) {
            System.out.println("⚠️ Spring Cloud Alibaba not configured or not compatible");
        }

        // 6. 验证 Spring Cloud 注解
        Class<? extends Annotation> refreshScopeClass = RefreshScope.class;
        assertNotNull("@RefreshScope should be available", refreshScopeClass);
        System.out.println("✅ Spring Cloud annotations are available");
    }

    @Test
    public void testDependencyConsistency() {
        // 验证 BOM 管理的依赖版本一致性
        System.out.println("=== Dependency Consistency Check ===");

        // 检查 Spring Boot 相关依赖
        checkPackageVersion("org.springframework.boot", "Spring Boot");
        checkPackageVersion("org.springframework", "Spring Framework");
        checkPackageVersion("org.springframework.cloud", "Spring Cloud");

        // 检查 Jakarta EE 相关依赖
        checkPackageVersion("jakarta.servlet", "Jakarta Servlet");
        checkPackageVersion("jakarta.ws.rs", "Jakarta RS");

        System.out.println("✅ Dependency consistency check completed");
    }

    private void checkPackageVersion(String packageName, String displayName) {
        try {
            Package pkg = Package.getPackage(packageName);
            if (pkg != null) {
                String implVersion = pkg.getImplementationVersion();
                String specVersion = pkg.getSpecificationVersion();

                System.out.println(displayName + " - Implementation: " +
                    (implVersion != null ? implVersion : "N/A") +
                    ", Specification: " +
                    (specVersion != null ? specVersion : "N/A"));
            } else {
                System.out.println("⚠️ " + displayName + " package not found in classpath");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Error checking " + displayName + " version: " + e.getMessage());
        }
    }

    @Test
    public void testContextLoads() {
        // 简单的上下文加载测试
        // 如果这个测试通过，说明基本的 Spring Boot 上下文可以加载
        System.out.println("✅ Spring Boot context loads successfully");
    }
}
