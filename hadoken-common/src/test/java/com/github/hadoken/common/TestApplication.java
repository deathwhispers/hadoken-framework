package com.github.hadoken.common;

import com.github.hadoken.common.config.EncryptProperties;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * 测试应用配置
 * <p>
 * 为common模块的Spring Boot测试提供配置
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2026/05/29
 */
@SpringBootConfiguration
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
@EnableConfigurationProperties(EncryptProperties.class)
@ComponentScan(basePackages = "com.github.hadoken.common")
public class TestApplication {
    // 空类，仅用于提供Spring Boot测试配置
}