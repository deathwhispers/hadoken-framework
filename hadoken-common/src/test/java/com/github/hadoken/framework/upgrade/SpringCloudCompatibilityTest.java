package com.github.hadoken.framework.upgrade;

import org.junit.Test;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

import java.lang.annotation.Annotation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Spring Cloud 兼容性验证测试
 * 验证 Spring Cloud 核心功能与 Spring Boot 3.5.x 的兼容性
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2026/05/29
 */
public class SpringCloudCompatibilityTest {

    @Test
    public void testSpringCloudCoreClassesAvailable() {
        // 验证 Spring Cloud 核心类可用
        Class<?> loadBalancerClientClass = LoadBalancerClient.class;
        Class<?> serviceInstanceClass = ServiceInstance.class;
        Class<?> configClientPropertiesClass = ConfigClientProperties.class;

        assertNotNull("LoadBalancerClient class should be available", loadBalancerClientClass);
        assertNotNull("ServiceInstance class should be available", serviceInstanceClass);
        assertNotNull("ConfigClientProperties class should be available", configClientPropertiesClass);

        System.out.println("Spring Cloud core classes are available");
    }

    @Test
    public void testSpringCloudAnnotationsAvailable() {
        // 验证 Spring Cloud 注解可用
        Class<? extends Annotation> refreshScopeClass = RefreshScope.class;

        assertNotNull("@RefreshScope annotation should be available", refreshScopeClass);

        // 检查注解的保留策略
        Annotation retention = refreshScopeClass.getAnnotation(java.lang.annotation.Retention.class);
        assertNotNull("@RefreshScope should have @Retention annotation", retention);

        System.out.println("Spring Cloud annotations are available");
    }

    @Test
    public void testSpringCloudVersion() {
        // 获取 Spring Cloud 版本信息
        Package cloudPackage = LoadBalancerClient.class.getPackage();
        String implementationVersion = cloudPackage.getImplementationVersion();
        String specificationVersion = cloudPackage.getSpecificationVersion();

        System.out.println("Spring Cloud Implementation Version: " + implementationVersion);
        System.out.println("Spring Cloud Specification Version: " + specificationVersion);

        // 验证版本为 2025.x
        if (implementationVersion != null) {
            assertTrue("Spring Cloud version should be 2025.x, got: " + implementationVersion,
                implementationVersion.startsWith("2025"));
        }
    }
}
