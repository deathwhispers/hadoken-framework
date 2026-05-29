# External Integrations

**Analysis Date:** 2026-05-29

## APIs & External Services

**消息中间件:**
- MQTT (Eclipse Paho) - IoT 消息通信
  - SDK/Client: `org.eclipse.paho.client.mqttv3` 1.2.5
  - 配置: `hadoken.mqtt.*` (`MqttConnectProperties.java`)
  - 支持发布/订阅模式，QoS 级别配置

- Redis MQ - 内部消息广播
  - 基于 Redis Pub/Sub 实现
  - SDK: Spring Data Redis + Redisson
  - 配置类: `HadokenMQAutoConfiguration.java`

**第三方服务 SDK (BOM 定义但未直接使用):**
- AWS SDK 2.30.14 - 云服务集成（可选）
- 微信 Java SDK 4.7.5.B - 微信集成（可选）
- JustAuth 1.16.7 - OAuth 第三方登录（可选）
- 积木报表 1.9.4 - 报表生成（可选）

## Data Storage

**Databases:**
- MySQL 8.x
  - Connection: `spring.datasource.*`
  - Driver: `com.mysql.cj.jdbc.Driver`
  - ORM: Spring Data JPA / MyBatis Plus
  - 连接池: Druid 1.2.24

- 国产数据库支持（可选）:
  - 达梦 DM8 - 驱动版本 8.1.3.140
  - 金仓 KingBase - 驱动版本 8.6.0
  - OpenGauss - 驱动版本 5.1.0
  - TDengine 时序数据库 - 驱动版本 3.3.3

**多数据源支持:**
- Dynamic Datasource 4.3.1
  - 配置类: `DataSourceAutoConfiguration.java`
  - 支持动态切换数据源

**File Storage:**
- 本地文件系统（通过 `FileUtil.java`）

**Caching:**
- Redis (Redisson 3.45.1)
  - 配置: `spring.data.redis.*`
  - 客户端: Redisson + Spring Data Redis
  - 序列化: String Key + JSON Value
  - 配置类: `HadokenRedisAutoConfiguration.java`

## Authentication & Identity

**Auth Provider:**
- Spring Security
  - 实现: `HadokenSecurityAutoConfiguration.java`
  - 密码加密: BCryptPasswordEncoder
  - 安全配置: `HadokenWebSecurityConfigurerAdapter.java`
  - 上下文策略: TransmittableThreadLocal（支持异步线程传递）

**JWT 支持 (BOM 定义):**
- JJWT 0.12.5 - JWT 令牌处理（可选集成）

## Monitoring & Observability

**APM 监控:**
- SkyWalking 9.0.0
  - Toolkit: `apm-toolkit-trace`, `apm-toolkit-logback-1.x`
  - OpenTracing 集成
  - 配置类: `HadokenTraceAutoConfiguration.java`
  - Trace Filter: 自动设置 traceId

**应用监控:**
- Spring Boot Admin 3.3.1
  - Client 模式集成
  - 配置: `hadoken.trace.*`

**指标收集:**
- Micrometer Core - 指标收集框架

**日志:**
- SLF4J + Logback（Spring Boot 默认）
- SkyWalking Logback 集成

## CI/CD & Deployment

**Hosting:**
- Spring Boot 应用（可部署到任意 JVM 环境）
- 支持 Docker/Kubernetes

**CI Pipeline:**
- 未检测到 CI 配置文件

**Maven 仓库:**
- 华为云镜像: `https://mirrors.huaweicloud.com/repository/maven/`
- 阿里云镜像: `https://maven.aliyun.com/repository/public`

## Environment Configuration

**Required env vars (示例配置):**
- `spring.datasource.url` - 数据库连接
- `spring.datasource.username` - 数据库用户名
- `spring.datasource.password` - 数据库密码
- `spring.data.redis.host` - Redis 地址
- `spring.data.redis.password` - Redis 密码

**自定义配置前缀:**
- `hadoken.redis.*` - Redis 扩展配置
- `hadoken.mqtt.*` - MQTT 配置
- `hadoken.scheduler.*` - 任务调度配置
- `hadoken.trace.*` - 监控追踪配置
- `hadoken.web.*` - Web 扩展配置

**Secrets location:**
- `application.yml`（开发环境）
- 生产环境应使用外部配置或密钥管理服务

## Webhooks & Callbacks

**Incoming:**
- Spring Boot Actuator Endpoints（可选）
- Scheduler REST API (`/api/scheduler/*`, 可配置)

**Outgoing:**
- 无内置 webhook 发送

## 工业物联网支持

**协议集成 (BOM 定义):**
- SNMP4J 3.8.0 - SNMP 网络管理协议
- Modbus4J 3.1.0 - Modbus 工业协议（注释状态）
- Netty 4.1.116.Final - 高性能网络框架

**模板引擎:**
- FreeMarker 2.3.31
- Velocity 2.4.1

**文件处理:**
- EasyExcel 4.0.3 - Excel 导出
- POI-TL 1.12.1 - Word 模板
- Apache POI 5.2.5 - Office 文件处理

## 分布式支持

**分布式锁:**
- Lock4j 2.2.7 - 分布式锁框架（Redisson 实现）

**任务调度:**
- 自研 Scheduler 模块
  - 存储: Memory / MyBatis / Redis
  - 支持分布式锁保护
  - REST 管理端点

**限流熔断:**
- Resilience4j RateLimiter - 限流保护

---

*Integration audit: 2026-05-29*