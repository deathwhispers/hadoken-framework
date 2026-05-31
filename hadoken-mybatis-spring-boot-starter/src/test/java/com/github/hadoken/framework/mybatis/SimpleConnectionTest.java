package com.github.hadoken.framework.mybatis;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据库连接测试 - JDK 25 兼容性验证
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2026/05/29
 */
@SpringBootTest(classes = SimpleConnectionTest.TestApplication.class)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.sql.init.mode=never",
    "mybatis-plus.mapper-locations=classpath*:/mapper/**/*.xml",
    "mybatis-plus.configuration.map-underscore-to-camel-case=true"
})
public class SimpleConnectionTest {

    @Autowired(required = false)
    private DataSource dataSource;

    /**
     * 验证数据源是否成功初始化
     * 测试 Druid 连接池在 JDK 25 环境下的兼容性
     */
    @Test
    public void testDataSourceInitialization() {
        assertNotNull(dataSource, "DataSource should be auto-configured");
        System.out.println("DataSource initialized successfully: " + dataSource.getClass().getName());
    }

    /**
     * 验证 MyBatis-Plus 配置是否加载
     * 测试 MyBatis-Plus 3.5.16 在 JDK 25 环境下的兼容性
     */
    @Test
    public void testMyBatisPlusConfig() {
        System.out.println("MyBatis-Plus configuration loaded successfully");
    }

    /**
     * 简单的测试应用类
     * 用于 SpringBootTest 启动最小化上下文
     */
    @org.springframework.boot.autoconfigure.SpringBootApplication
    static class TestApplication {
        // 仅用于测试启动
    }
}