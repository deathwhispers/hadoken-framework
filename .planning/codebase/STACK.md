# Technology Stack

**Analysis Date:** 2026-05-29

## Languages

**Primary:**
- Java 17 - 项目目标编译版本（定义于 `pom.xml`）
- 实际运行环境：Java 21 (OpenJDK Temurin-21.0.11)

**Secondary:**
- 无

## Runtime

**Environment:**
- JVM (OpenJDK 21.0.11 LTS)
- Spring Boot 3.3.13

**Package Manager:**
- Maven 3.6.3
- Lockfile: 无独立 lockfile，使用 pom.xml 依赖管理

## Frameworks

**Core:**
- Spring Boot 3.3.13 - 应用框架核心
- Spring Cloud 2025.0.0 - 微服务支持
- Spring Cloud Alibaba 2023.0.1.0 - 阿里云微服务组件

**Testing:**
- Spring Boot Test - 测试框架
- JUnit 4.12 - 单元测试
- Mockito Inline 5.2.0 - Mock 框架
- Jedis Mock 1.1.11 - Redis 模拟
- Podam 8.0.2.RELEASE - 测试数据生成

**Build/Dev:**
- Maven Compiler Plugin 3.12.1 - 编译
- Maven Resources Plugin 3.3.1 - 资源处理
- Maven Source Plugin 3.3.0 - 源码打包

## Key Dependencies

**Critical:**
- Hutool 5.8.39 - Java 工具库
- Lombok 1.18.38 - 代码简化
- MapStruct 1.6.3 - 对象映射
- Guava 33.4.8-jre - Google 工具库
- Fastjson2 2.0.57 - JSON 处理
- Jackson - JSON 序列化（Spring Boot 内置）

**Infrastructure:**
- Druid 1.2.24 - 数据库连接池
- MyBatis Plus 3.5.12 - ORM 框架
- Redisson 3.45.1 - Redis 客户端
- Spring Data Redis - Redis 集成
- MySQL Connector 8.4.0 - MySQL 驱动

**API Documentation:**
- SpringDoc OpenAPI 2.6.0 - OpenAPI 文档
- Knife4j 4.5.0 - API 文档增强 UI

**Security:**
- Spring Security - 安全框架
- BCryptPasswordEncoder - 密码加密

**Observability:**
- SkyWalking APM Toolkit 9.0.0 - APM 集成
- Spring Boot Admin 3.3.1 - 应用监控
- Micrometer - 指标收集

## Configuration

**Environment:**
- Spring Boot 配置文件：`application.yml`
- 自定义配置前缀：`hadoken.*`
- Maven 仓库：华为云镜像、阿里云镜像

**Build:**
- 主 pom.xml: `/pom.xml`
- 依赖管理 BOM: `hadoken-dependencies/pom.xml`
- 编码：UTF-8

## Platform Requirements

**Development:**
- JDK 17+（推荐 21）
- Maven 3.6+

**Production:**
- 支持 Docker/Kubernetes 部署（通过 Spring Boot）
- 需要 MySQL 8.x 数据库
- 需要 Redis 服务（支持 Redisson）
- 可选：MQTT Broker、消息队列

## Project Modules

项目采用多模块架构，包含 13 个子模块：

| 模块 | 用途 |
|------|------|
| `hadoken-common` | 公共工具类 |
| `hadoken-dependencies` | BOM 依赖版本管理 |
| `hadoken-web-spring-boot-starter` | Web MVC 增强 |
| `hadoken-security-spring-boot-starter` | Spring Security 增强 |
| `hadoken-jpa-spring-boot-starter` | JPA 数据访问 |
| `hadoken-mybatis-spring-boot-starter` | MyBatis Plus 增强 |
| `hadoken-redis-spring-boot-starter` | Redis 增强 |
| `hadoken-mq-spring-boot-starter` | Redis 消息队列 |
| `hadoken-mqtt-spring-boot-starter` | MQTT 集成 |
| `hadoken-scheduler-spring-boot-starter` | 定时任务调度 |
| `hadoken-monitor-spring-boot-starter` | 监控追踪 |
| `hadoken-stats-spring-boot-starter` | 统计分析 |
| `hadoken-websocket-spring-boot-starter` | WebSocket 支持 |
| `hadoken-test` | 测试模块 |

---

*Stack analysis: 2026-05-29*