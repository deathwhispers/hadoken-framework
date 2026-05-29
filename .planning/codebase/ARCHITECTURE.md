<!-- refreshed: 2026-05-29 -->
# Architecture

**Analysis Date:** 2026-05-29

## System Overview

```text
┌─────────────────────────────────────────────────────────────────────────────┐
│                     Spring Boot Application Layer                            │
│                    (业务应用层 - 使用 Starter 模块)                           │
├─────────────────────────────────────────────────────────────────────────────┤
│                       Hadoken Framework Starters                            │
├──────────┬──────────┬──────────┬──────────┬──────────┬──────────┬──────────┤
│   Web    │ Security │  MyBatis │   Redis  │    MQ    │ Scheduler│ WebSocket│
│ Starter  │ Starter  │ Starter  │ Starter  │ Starter  │ Starter  │ Starter  │
│`hadoken- │`hadoken- │`hadoken- │`hadoken- │`hadoken- │`hadoken- │`hadoken- │
│ web...`  │security..│mybatis...│redis...` │mq...`    │scheduler │websocket │
├──────────┴──────────┴──────────┴──────────┴──────────┴──────────┴──────────┤
│                     Hadoken Common (公共模块)                                │
│                    `hadoken-common`                                          │
│         ├─ result (CommonResult)    ├─ exception (HadokenServiceException) │
│         ├─ enums (错误码/状态)       ├─ util (工具类集合)                    │
│         ├─ entity (PageResult等)    ├─ core (核心接口)                      │
└─────────────────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                     Spring Boot / Spring Cloud                               │
│                    (框架底层)                                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│  Spring Boot 3.3.13 | Spring Cloud 2025.0.0 | Spring Cloud Alibaba 2023.0.1 │
└─────────────────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                     External Infrastructure                                  │
├──────────┬──────────┬──────────┬──────────┬──────────┬──────────────────────┤
│  MySQL   │  Redis   │ RocketMQ │  MQTT    │ SkyWalking│ Flowable BPM        │
│ MyBatis+ │ Redisson │ Kafka    │ Eclipse  │ APM       │ Workflow Engine     │
└──────────┴──────────┴──────────┴──────────┴──────────┴──────────────────────┘
```

## Component Responsibilities

| Component | Responsibility | File |
|-----------|----------------|------|
| hadoken-common | 公共基础模块,包含统一响应、异常、工具类、枚举等 | `hadoken-common/pom.xml` |
| hadoken-dependencies | BOM 依赖管理,统一所有依赖版本 | `hadoken-dependencies/pom.xml` |
| hadoken-web-spring-boot-starter | Web层增强:全局异常处理、CORS、XSS防护、API日志、数据脱敏、加密 | `hadoken-web-spring-boot-starter/src/main/java/...` |
| hadoken-security-spring-boot-starter | Spring Security 增强:认证切面、权限处理、安全上下文策略 | `hadoken-security-spring-boot-starter/src/main/java/...` |
| hadoken-mybatis-spring-boot-starter | MyBatis-Plus 增强:分页、字段填充、加密解密、条件查询扩展 | `hadoken-mybatis-spring-boot-starter/src/main/java/...` |
| hadoken-redis-spring-boot-starter | Redis 增强:缓存管理、序列化配置、工具类封装 | `hadoken-redis-spring-boot-starter/src/main/java/...` |
| hadoken-mq-spring-boot-starter | 消息队列:Redis Pub/Sub消息模板、拦截器、监听器基类 | `hadoken-mq-spring-boot-starter/src/main/java/...` |
| hadoken-scheduler-spring-boot-starter | 轻量级任务调度:多存储支持(MyBatis/Redis/Memory)、分布式锁、管理端点 | `hadoken-scheduler-spring-boot-starter/src/main/java/...` |
| hadoken-websocket-spring-boot-starter | WebSocket 增强:STOMP协议、心跳配置、消息发送器 | `hadoken-websocket-spring-boot-starter/src/main/java/...` |
| hadoken-monitor-spring-boot-starter | 监控追踪:SkyWalking集成、业务追踪切面、TraceId传递 | `hadoken-monitor-spring-boot-starter/src/main/java/...` |
| hadoken-mqtt-spring-boot-starter | MQTT集成:Eclipse Paho客户端、连接配置、QoS管理 | `hadoken-mqtt-spring-boot-starter/src/main/java/...` |
| hadoken-stats-spring-boot-starter | 统计分析:时间段解析器、同比环比计算、时序数据模型 | `hadoken-stats-spring-boot-starter/src/main/java/...` |
| hadoken-jpa-spring-boot-starter | JPA增强:基础实体、DTO、查询辅助、数据权限 | `hadoken-jpa-spring-boot-starter/src/main/java/...` |
| hadoken-test | 测试模块:调度器测试、锁提供者配置 | `hadoken-test/src/main/java/...` |

## Pattern Overview

**Overall:** Spring Boot Starter 模块化架构

**Key Characteristics:**
- 每个模块采用 `*-spring-boot-starter` 命名,遵循 Spring Boot 官方 Starter 规范
- 使用 `@AutoConfiguration` + `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 实现自动装配
- 配置属性类统一命名为 `*Properties`,使用 `@EnableConfigurationProperties` 注入
- 条件装配使用 `@ConditionalOnProperty`、`@ConditionalOnMissingBean`、`@ConditionalOnClass` 等
- 统一依赖版本通过 `hadoken-dependencies` BOM 管理

## Layers

**公共基础层 (hadoken-common):**
- Purpose: 提供框架级公共组件,被所有 Starter 模块依赖
- Location: `hadoken-common/src/main/java/com/github/hadoken/common/`
- Contains: 
  - 统一响应封装 `CommonResult`
  - 业务异常体系 `HadokenServiceException`
  - 全局错误码枚举 `GlobalErrorCodeConstants`
  - 工具类集合(日期、字符串、JSON、加密等)
  - 分页实体 `PageResult`、`PageParam`
  - 枚举定义(状态、用户类型、过滤器顺序等)
- Depends on: Spring Core、Jackson、Hutool、Lombok、MapStruct
- Used by: 所有 Starter 模块

**Starter 自动配置层:**
- Purpose: Spring Boot 自动装配,提供各领域功能增强
- Location: 各模块 `autoconfigure` 包
- Contains: 
  - `*AutoConfiguration` 配置类
  - `*Properties` 配置属性类
- Depends on: hadoken-common、Spring Boot 相关依赖
- Used by: 业务应用直接引入 Starter

**核心功能层 (core):**
- Purpose: 各模块核心实现逻辑
- Location: 各模块 `core` 包
- Contains:
  - 过滤器 (Filter)
  - 处理器 (Handler)
  - 切面 (Aspect)
  - 工具类 (Utils)
  - 注解 (Annotation)
  - 消息模型 (Message)
- Depends on: 各模块的 autoconfigure 和公共层

## Data Flow

### Primary HTTP Request Path

1. HTTP请求到达 (`Filter链入口`)
2. **TraceFilter** 设置 TraceId (`hadoken-monitor-spring-boot-starter/src/main/java/com/github/hadoken/framework/monitor/core/filter/TracerFilter.java`)
3. **CacheRequestBodyFilter** 包装请求体支持重复读取 (`hadoken-web-spring-boot-starter/src/main/java/com/github/hadoken/framework/web/mvc/core/filter/CacheRequestBodyFilter.java`)
4. **XssFilter** XSS攻击防护 (`hadoken-web-spring-boot-starter/src/main/java/com/github/hadoken/framework/web/xss/core/filter/XssFilter.java`)
5. **ApiAccessLogFilter** 记录API访问日志 (`hadoken-web-spring-boot-starter/src/main/java/com/github/hadoken/framework/web/apilog/core/filter/ApiAccessLogFilter.java`)
6. **CorsFilter** 跨域处理 (`hadoken-web-spring-boot-starter/src/main/java/com/github/hadoken/framework/web/mvc/autoconfigure/HadokenWebAutoConfiguration.java:59-92`)
7. Controller 处理业务逻辑
8. **GlobalResponseBodyHandler** 统一响应处理 (如有)
9. **GlobalExceptionHandler** 异常兜底处理 (`hadoken-web-spring-boot-starter/src/main/java/com/github/hadoken/framework/web/mvc/core/handler/GlobalExceptionHandler.java`)

### 异常处理流程

1. Controller 抛出异常
2. **GlobalExceptionHandler** 拦截 (`GlobalExceptionHandler.java:62-97`)
3. 根据异常类型匹配处理方法:
   - `HadokenServiceException` → 返回业务错误码
   - `ValidationException` → 返回 400 BAD_REQUEST
   - `AccessDeniedException` → 返回 403 FORBIDDEN
   - 其他 Exception → 返回 500 INTERNAL_SERVER_ERROR 并记录错误日志
4. 返回 `CommonResult<T>` 统一响应格式

### 消息队列流程 (Redis MQ)

1. 生产者调用 `RedisMQTemplate.send(message)` (`hadoken-mq-spring-boot-starter/src/main/java/com/github/hadoken/framework/mq/core/RedisMQTemplate.java`)
2. 消息经过拦截器链处理 (`RedisMessageInterceptor`)
3. 发布到 Redis Channel
4. **RedisMessageListenerContainer** 监听消息 (`hadoken-mq-spring-boot-starter/src/main/java/com/github/hadoken/framework/mq/autoconfigure/HadokenMQAutoConfiguration.java:48-62`)
5. **AbstractChannelMessageListener** 处理消息 (`hadoken-mq-spring-boot-starter/src/main/java/com/github/hadoken/framework/mq/core/pubsub/AbstractChannelMessageListener.java`)

### 任务调度流程

1. **TaskManager** 管理任务 (`hadoken-scheduler-spring-boot-starter/src/main/java/com/github/hadoken/framework/scheduler/manager/TaskManagerImpl.java`)
2. 根据配置选择存储实现:
   - `MybatisTaskStore` → 数据库持久化
   - `RedisTaskStore` → Redis持久化
   - `InMemoryTaskStore` → 内存存储
3. **ThreadPoolTaskScheduler** 执行定时任务
4. **TaskLogStore** 记录执行日志
5. **SchedulerController** 提供管理端点 (可选)

**State Management:**
- Security上下文: 使用 `TransmittableThreadLocalSecurityContextHolderStrategy` 支持异步线程传递 (`hadoken-security-spring-boot-starter/src/main/java/com/github/hadoken/framework/security/core/context/TransmittableThreadLocalSecurityContextHolderStrategy.java`)
- TraceId: 通过 `TransmittableThreadLocal` 在线程池中传递 (`hadoken-common/src/main/java/com/github/hadoken/common/util/monitor/TracerUtils.java`)

## Key Abstractions

**CommonResult - 统一响应封装:**
- Purpose: 所有 API 返回统一格式 {code, data, msg}
- Examples: `hadoken-common/src/main/java/com/github/hadoken/common/result/CommonResult.java`
- Pattern: 静态工厂方法 `success(T data)` / `error(ErrorCode)` / `error(Integer code, String message)`

**HadokenServiceException - 业务异常:**
- Purpose: 携带业务错误码的业务异常,区别于系统异常
- Examples: `hadoken-common/src/main/java/com/github/hadoken/common/exception/HadokenServiceException.java`
- Pattern: 包含 `code` 和 `message`,与 `ErrorCode` 接口配合使用

**QueryWrapperX - 条件查询扩展:**
- Purpose: MyBatis-Plus QueryWrapper 增强,支持条件存在时才添加查询条件
- Examples: `hadoken-mybatis-spring-boot-starter/src/main/java/com/github/hadoken/framework/mybatis/core/condition/query/QueryWrapperX.java`
- Pattern: `eqIfPresent()`、`likeIfPresent()`、`inIfPresent()` 等方法,参数为空时忽略条件

**AbstractChannelMessageListener - 消息监听基类:**
- Purpose: Redis Pub/Sub 消息监听抽象基类
- Examples: `hadoken-mq-spring-boot-starter/src/main/java/com/github/hadoken/framework/mq/core/pubsub/AbstractChannelMessageListener.java`
- Pattern: 实现 `onMessage()` 方法处理消息,自动注册到监听容器

**BaseMapperPlus - Mapper 增强:**
- Purpose: MyBatis-Plus Mapper 基类扩展
- Examples: `hadoken-mybatis-spring-boot-starter/src/main/java/com/github/hadoken/framework/mybatis/core/mapper/BaseMapperPlus.java`
- Pattern: 继承 MyBatis-Plus BaseMapper,添加批量操作等增强方法

## Entry Points

**Spring Boot AutoConfiguration 入口:**
- Location: `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- Triggers: Spring Boot 应用启动时自动扫描并加载
- Responsibilities: 注册各模块的核心 Bean

**Web 模块入口:**
- Location: `hadoken-web-spring-boot-starter/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- 注册 AutoConfiguration:
  - `HadokenApiLogAutoConfiguration` - API日志
  - `HadokenApiEncryptAutoConfiguration` - API加密
  - `HadokenJacksonAutoConfiguration` - Jackson配置
  - `HadokenWebAutoConfiguration` - Web核心配置
  - `HadokenSwaggerAutoConfiguration` - Swagger/Knife4j
  - `HadokenXssAutoConfiguration` - XSS防护

**MyBatis 模块入口:**
- Location: `hadoken-mybatis-spring-boot-starter/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- 注册: `DataSourceAutoConfiguration`, `MybatisAutoConfiguration`
- 使用 `@MapperScan` 自动扫描 Mapper 接口

## Architectural Constraints

- **Java Version:** 强制 Java 17+,配置在 `pom.xml:16` `<java.version>17</java.version>`
- **Spring Boot Version:** 固定 3.3.13,通过 BOM 管理 (`hadoken-dependencies/pom.xml:31`)
- **依赖版本:** 所有版本统一在 `hadoken-dependencies/pom.xml` 管理,业务模块禁止直接指定版本
- **模块依赖:** Starter 模块可依赖 hadoken-common,不可相互依赖(避免循环)
- **包命名:** 所有代码位于 `com.github.hadoken.*` 包下
- **Global state:** 
  - `TransmittableThreadLocal` 用于 TraceId 和 SecurityContext 传递 (`hadoken-common/src/main/java/com/github/hadoken/common/util/monitor/TracerUtils.java`)
  - `RequestHolder` 用于请求上下文持有 (`hadoken-common/src/main/java/com/github/hadoken/common/util/RequestHolder.java`)
- **Threading:** 默认单线程事件循环,WebSocket 使用独立线程池 (`hadoken-websocket-spring-boot-starter/src/main/java/com/github/hadoken/framework/websocket/autoconfigure/HadokenWebSocketAutoConfiguration.java:97-104`)
- **Circular imports:** 无已知循环依赖,模块设计遵循单向依赖原则

## Anti-Patterns

### 空实现 Service

**What happens:** 部分服务接口提供默认空实现,如 `DefaultApiErrorLogService`
**Why it's wrong:** 可能导致错误日志丢失,生产环境应替换为实际实现
**Do this instead:** 业务应用应实现 `ApiErrorLogService` 并注入,或配置使用数据库存储
- 参考: `hadoken-web-spring-boot-starter/src/main/java/com/github/hadoken/framework/web/apilog/core/service/DefaultApiErrorLogService.java`

### 内存存储警告

**What happens:** Scheduler 使用 MEMORY 存储时仅打印警告日志
**Why it's wrong:** 内存存储重启后任务丢失,不适合生产环境
**Do this instead:** 配置 `hadoken.scheduler.store.type=MYBATIS` 或 `REDIS`
- 参考: `hadoken-scheduler-spring-boot-starter/src/main/java/com/github/hadoken/framework/scheduler/autoconfigure/HadokenSchedulerAutoConfiguration.java:77`

## Error Handling

**Strategy:** 全局异常处理器 + 统一错误码体系

**Patterns:**
- **全局异常处理器:** `GlobalExceptionHandler` 使用 `@RestControllerAdvice` 拦截所有异常 (`hadoken-web-spring-boot-starter/src/main/java/com/github/hadoken/framework/web/mvc/core/handler/GlobalExceptionHandler.java`)
- **错误码枚举:** `GlobalErrorCodeConstants` 定义 HTTP 状态码对应的错误码 (`hadoken-common/src/main/java/com/github/hadoken/common/enums/GlobalErrorCodeConstants.java`)
- **业务异常:** `HadokenServiceException` 携带业务错误码抛出 (`hadoken-common/src/main/java/com/github/hadoken/common/exception/HadokenServiceException.java`)
- **错误日志:** 异常发生时调用 `ApiErrorLogService.createApiErrorLogAsync()` 异步记录

## Cross-Cutting Concerns

**Logging:** 使用 Slf4j + Logback,SkyWalking集成日志追踪 (`apm-toolkit-logback-1-x`)
**Validation:** Jakarta Validation + Spring `@Validated`,错误由 `GlobalExceptionHandler` 统一处理
**Authentication:** Spring Security + JWT,通过 `PreAuthenticatedAspect` 切面校验 (`hadoken-security-spring-boot-starter/src/main/java/com/github/hadoken/framework/security/core/aop/PreAuthenticatedAspect.java`)
**Tracing:** SkyWalking APM 集成,TraceId 通过 Filter 设置并传递 (`hadoken-monitor-spring-boot-starter`)
**Desensitization:** 数据脱敏注解 + Jackson 序列化器 (`hadoken-web-spring-boot-starter/src/main/java/com/github/hadoken/framework/web/desensitize/`)
**Encryption:** API 加解密 Filter + RSA/AES (`hadoken-web-spring-boot-starter/src/main/java/com/github/hadoken/framework/web/encrypt/`)

---

*Architecture analysis: 2026-05-29*