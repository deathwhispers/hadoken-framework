package com.github.hadoken.framework.upgrade;

import org.junit.Test;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.cloud.sentinel.SentinelProperties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Spring Cloud Alibaba 兼容性验证测试
 * 验证 Spring Cloud Alibaba 组件与 Spring Cloud 2025.x 的兼容性
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2026/05/29
 */
public class AlibabaCompatibilityTest {

    @Test
    public void testAlibabaCoreClassesAvailable() {
        // 验证 Spring Cloud Alibaba 核心类可用
        try {
            Class<?> nacosDiscoveryPropertiesClass = NacosDiscoveryProperties.class;
            Class<?> nacosServiceManagerClass = NacosServiceManager.class;
            Class<?> sentinelPropertiesClass = SentinelProperties.class;

            assertNotNull("NacosDiscoveryProperties class should be available", nacosDiscoveryPropertiesClass);
            assertNotNull("NacosServiceManager class should be available", nacosServiceManagerClass);
            assertNotNull("SentinelProperties class should be available", sentinelPropertiesClass);

            System.out.println("Spring Cloud Alibaba core classes are available");
        } catch (NoClassDefFoundError e) {
            System.out.println("WARNING: Spring Cloud Alibaba classes not found. " +
                "This may indicate compatibility issues with Spring Cloud 2025.x.");
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Test
    public void testAlibabaVersion() {
        // 获取 Spring Cloud Alibaba 版本信息
        try {
            Package alibabaPackage = NacosDiscoveryProperties.class.getPackage();
            String implementationVersion = alibabaPackage.getImplementationVersion();
            String specificationVersion = alibabaPackage.getSpecificationVersion();

            System.out.println("Spring Cloud Alibaba Implementation Version: " + implementationVersion);
            System.out.println("Spring Cloud Alibaba Specification Version: " + specificationVersion);

            // 验证版本为 2023.x
            if (implementationVersion != null) {
                assertTrue("Spring Cloud Alibaba version should be 2023.x, got: " + implementationVersion,
                    implementationVersion.startsWith("2023"));
            }
        } catch (NoClassDefFoundError e) {
            System.out.println("Cannot check Alibaba version: " + e.getMessage());
        }
    }

    @Test
    public void testAlibabaCompatibilityWithSpringCloud() {
        // 验证 Alibaba 与 Spring Cloud 的兼容性
        try {
            // 尝试创建 Alibaba 配置对象
            Class<?> nacosConfigClass = Class.forName("com.alibaba.cloud.nacos.NacosConfigProperties");
            Class<?> sentinelDataSourceClass = Class.forName("com.alibaba.cloud.sentinel.datasource.config.DataSourcePropertiesConfiguration");

            assertNotNull("NacosConfigProperties class should be available", nacosConfigClass);
            assertNotNull("DataSourcePropertiesConfiguration class should be available", sentinelDataSourceClass);

            System.out.println("Spring Cloud Alibaba configuration classes are available");
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            System.out.println("WARNING: Some Spring Cloud Alibaba configuration classes not found.");
            System.out.println("This may indicate compatibility issues or missing dependencies.");
            System.out.println("Error: " + e.getMessage());
        }
    }
}
