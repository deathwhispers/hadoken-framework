<!-- GSD:project-start source:PROJECT.md -->
## Project

**Hadoken Framework**

企业级 Spring Boot Starter 集合框架，提供 Web、Security、MyBatis、Redis、MQ、Scheduler、WebSocket、Monitor 等模块的增强功能，简化业务应用开发。

**升级目标:** 将 JDK 从 17 升级到 25，Spring Boot 升级到匹配的稳定版本，引入 JDK 25 新特性代码规范。

**Core Value:** 提供开箱即用的企业级 Spring Boot Starter 模块，让业务应用只需引入依赖即可获得完整的基础功能支持。

### Constraints

- **JDK 版本:** 必须升级到 JDK 25
- **Spring Boot 版本:** 必须是支持 JDK 25 的稳定版本
- **兼容性:** 不需要保持 JDK 17 兼容性
- **模块依赖:** Starter 模块可依赖 hadoken-common，不可相互依赖
<!-- GSD:project-end -->

<!-- GSD:stack-start source:codebase/STACK.md -->
## Technology Stack

## Languages
- Java 17 - 项目目标编译版本（定义于 `pom.xml`）
- 实际运行环境：Java 21 (OpenJDK Temurin-21.0.11)
- 无
## Runtime
- JVM (OpenJDK 21.0.11 LTS)
- Spring Boot 3.3.13
- Maven 3.6.3
- Lockfile: 无独立 lockfile，使用 pom.xml 依赖管理
## Frameworks
- Spring Boot 3.3.13 - 应用框架核心
- Spring Cloud 2025.0.0 - 微服务支持
- Spring Cloud Alibaba 2023.0.1.0 - 阿里云微服务组件
- Spring Boot Test - 测试框架
- JUnit 4.12 - 单元测试
- Mockito Inline 5.2.0 - Mock 框架
- Jedis Mock 1.1.11 - Redis 模拟
- Podam 8.0.2.RELEASE - 测试数据生成
- Maven Compiler Plugin 3.12.1 - 编译
- Maven Resources Plugin 3.3.1 - 资源处理
- Maven Source Plugin 3.3.0 - 源码打包
## Key Dependencies
- Hutool 5.8.39 - Java 工具库
- Lombok 1.18.38 - 代码简化
- MapStruct 1.6.3 - 对象映射
- Guava 33.4.8-jre - Google 工具库
- Fastjson2 2.0.57 - JSON 处理
- Jackson - JSON 序列化（Spring Boot 内置）
- Druid 1.2.24 - 数据库连接池
- MyBatis Plus 3.5.12 - ORM 框架
- Redisson 3.45.1 - Redis 客户端
- Spring Data Redis - Redis 集成
- MySQL Connector 8.4.0 - MySQL 驱动
- SpringDoc OpenAPI 2.6.0 - OpenAPI 文档
- Knife4j 4.5.0 - API 文档增强 UI
- Spring Security - 安全框架
- BCryptPasswordEncoder - 密码加密
- SkyWalking APM Toolkit 9.0.0 - APM 集成
- Spring Boot Admin 3.3.1 - 应用监控
- Micrometer - 指标收集
## Configuration
- Spring Boot 配置文件：`application.yml`
- 自定义配置前缀：`hadoken.*`
- Maven 仓库：华为云镜像、阿里云镜像
- 主 pom.xml: `/pom.xml`
- 依赖管理 BOM: `hadoken-dependencies/pom.xml`
- 编码：UTF-8
## Platform Requirements
- JDK 17+（推荐 21）
- Maven 3.6+
- 支持 Docker/Kubernetes 部署（通过 Spring Boot）
- 需要 MySQL 8.x 数据库
- 需要 Redis 服务（支持 Redisson）
- 可选：MQTT Broker、消息队列
## Project Modules
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
<!-- GSD:stack-end -->

<!-- GSD:conventions-start source:CONVENTIONS.md -->
## Conventions

## Naming Patterns
- Java files: PascalCase matching class name (e.g., `TaskManagerImpl.java`, `RedisUtils.java`)
- Package-info files: `package-info.java` for package-level documentation
- Configuration files: `application.yml`, `pom.xml`
- Interface: Plain name without prefix (e.g., `TaskStore`, `ApiAccessLogService`)
- Implementation: `Impl` suffix or `Default` prefix (e.g., `TaskManagerImpl`, `DefaultApiAccessLogService`)
- DTO: `DTO` suffix (e.g., `ApiAccessLogDTO`)
- Entity/Model: No suffix (e.g., `TaskDefinition`, `PageResult`)
- Utility: `Utils` suffix (e.g., `RedisUtils`, `JsonUtils`, `StringUtils`)
- Exception: `Exception` suffix (e.g., `HadokenServiceException`, `BadRequestException`)
- Enum: `Enum` suffix optional, plain name common (e.g., `TaskStatus`, `CommonStatusEnum`)
- Auto-configuration: `AutoConfiguration` suffix (e.g., `HadokenRedisAutoConfiguration`)
- Properties: `Properties` suffix (e.g., `HadokenRedisProperties`, `WebSocketProperties`)
- Interceptor: `Interceptor` suffix (e.g., `ParameterInterceptor`, `ResultSetInterceptor`)
- Annotation: Plain name or descriptive (e.g., `ApiAccessLog`, `SensitiveData`, `EncryptTransaction`)
- camelCase (e.g., `findById`, `createApiAccessLogAsync`, `getTaskOrThrow`)
- Boolean getters: `isXxx()` pattern (e.g., `isSuccess()`, `isError()`)
- Factory methods: `of`, `empty`, `error`, `success` (e.g., `CommonResult.success()`, `PageResult.empty()`)
- Async methods: `Async` suffix (e.g., `createApiAccessLogAsync`)
- camelCase for local variables and fields
- Constants: UPPER_SNAKE_CASE (e.g., `MESSAGES`, `UNKNOWN`, `SEPARATOR`)
- Private fields: camelCase, typically with Lombok annotations
- Java 17 records for simple immutable data (e.g., `ErrorCode` as `record ErrorCode(Integer code, String msg)`)
- Lombok `@Data` for mutable entities
- Lombok `@Builder` for builder pattern
## Code Style
- UTF-8 encoding throughout
- Java 17 source/target
- No explicit checkstyle or formatter config files detected
- Standard Java indentation (4 spaces implied)
- `@SuppressWarnings` annotations used selectively for:
## Import Organization
- No path aliases detected
- Full package imports used throughout
## Error Handling
- Business exceptions: `HadokenServiceException` with `ErrorCode`
- ErrorCode uses Java record: `record ErrorCode(Integer code, String msg)`
- Error codes follow HTTP status conventions (400, 401, 403, 404, 500, etc.)
- Error message formatting uses `{}` placeholder (slf4j-style) via `HadokenServiceExceptionUtil`
- Global exception handler: `GlobalExceptionHandler` with `@RestControllerAdvice`
- `@ExceptionHandler(MissingServletRequestParameterException.class)` - parameter missing
- `@ExceptionHandler(MethodArgumentNotValidException.class)` - validation errors
- `@ExceptionHandler(ConstraintViolationException.class)` - constraint violations
- `@ExceptionHandler(HadokenServiceException.class)` - business exceptions
- `@ExceptionHandler(AccessDeniedException.class)` - permission denied
- `@ExceptionHandler(Exception.class)` - catch-all for system errors
## Logging
- Use Chinese for user-facing messages
- Include relevant context in log messages
- Exception parameter passed separately for stack trace: `log.error("message", ex)`
- Structured logging with placeholders: `log.info("操作完成: {}", value)`
## Comments
- All public classes and interfaces require Javadoc
- All public methods require Javadoc
- Complex logic requires inline comments
- Package documentation via `package-info.java`
- Description in Chinese
- `@author yanggj` (consistent author)
- `@version 1.0.0`
- `@date YYYY/MM/DD HH:mm`
- Chinese for explanations
- English for technical terms
- Section separators with comment blocks: `// ========== Section Name ==========`
## Function Design
- Use `@Valid` for validation in service interfaces: `void createApiAccessLogAsync(@Valid ApiAccessLogDTO createDTO)`
- DTO objects for multiple parameters
- Optional via Java `Optional<T>` for nullable returns
- `CommonResult<T>` for API responses
- `Optional<T>` for nullable queries (e.g., `Optional<TaskDefinition> findById(String taskId)`)
- `List<T>` for collections, never null (empty list returned)
- Boolean methods use `isXxx()` pattern
## Module Design
- Public interfaces for contracts
- Implementation classes in same package or sub-package
- Auto-configuration classes exposed via Spring Boot mechanism
- `package-info.java` for package documentation
- No explicit barrel/index files
## Lombok Usage
- `@Data` - getters, setters, toString, equals, hashCode
- `@Slf4j` - logger field
- `@Builder` - builder pattern
- `@NoArgsConstructor` / `@AllArgsConstructor` - constructor generation
- `@Getter` / `@Setter` - selective property accessors
- `@SneakyThrows` - exception wrapping
## Dependency Management
- `provided` - used only by utilities, not packaged (e.g., `spring-core`, `spring-expression`)
- `optional` - annotation processors (e.g., `spring-boot-configuration-processor`)
- `compile` - core dependencies
- `test` - test-only dependencies
- All versions in `hadoken-dependencies/pom.xml` BOM
- Properties for each version: `<redisson.version>3.45.1</redisson.version>`
- Import via `<scope>import</scope>` in dependencyManagement
<!-- GSD:conventions-end -->

<!-- GSD:architecture-start source:ARCHITECTURE.md -->
## Architecture

## System Overview
```text
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
- 每个模块采用 `*-spring-boot-starter` 命名,遵循 Spring Boot 官方 Starter 规范
- 使用 `@AutoConfiguration` + `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 实现自动装配
- 配置属性类统一命名为 `*Properties`,使用 `@EnableConfigurationProperties` 注入
- 条件装配使用 `@ConditionalOnProperty`、`@ConditionalOnMissingBean`、`@ConditionalOnClass` 等
- 统一依赖版本通过 `hadoken-dependencies` BOM 管理
## Layers
- Purpose: 提供框架级公共组件,被所有 Starter 模块依赖
- Location: `hadoken-common/src/main/java/com/github/hadoken/common/`
- Contains: 
- Depends on: Spring Core、Jackson、Hutool、Lombok、MapStruct
- Used by: 所有 Starter 模块
- Purpose: Spring Boot 自动装配,提供各领域功能增强
- Location: 各模块 `autoconfigure` 包
- Contains: 
- Depends on: hadoken-common、Spring Boot 相关依赖
- Used by: 业务应用直接引入 Starter
- Purpose: 各模块核心实现逻辑
- Location: 各模块 `core` 包
- Contains:
- Depends on: 各模块的 autoconfigure 和公共层
## Data Flow
### Primary HTTP Request Path
### 异常处理流程
### 消息队列流程 (Redis MQ)
### 任务调度流程
- Security上下文: 使用 `TransmittableThreadLocalSecurityContextHolderStrategy` 支持异步线程传递 (`hadoken-security-spring-boot-starter/src/main/java/com/github/hadoken/framework/security/core/context/TransmittableThreadLocalSecurityContextHolderStrategy.java`)
- TraceId: 通过 `TransmittableThreadLocal` 在线程池中传递 (`hadoken-common/src/main/java/com/github/hadoken/common/util/monitor/TracerUtils.java`)
## Key Abstractions
- Purpose: 所有 API 返回统一格式 {code, data, msg}
- Examples: `hadoken-common/src/main/java/com/github/hadoken/common/result/CommonResult.java`
- Pattern: 静态工厂方法 `success(T data)` / `error(ErrorCode)` / `error(Integer code, String message)`
- Purpose: 携带业务错误码的业务异常,区别于系统异常
- Examples: `hadoken-common/src/main/java/com/github/hadoken/common/exception/HadokenServiceException.java`
- Pattern: 包含 `code` 和 `message`,与 `ErrorCode` 接口配合使用
- Purpose: MyBatis-Plus QueryWrapper 增强,支持条件存在时才添加查询条件
- Examples: `hadoken-mybatis-spring-boot-starter/src/main/java/com/github/hadoken/framework/mybatis/core/condition/query/QueryWrapperX.java`
- Pattern: `eqIfPresent()`、`likeIfPresent()`、`inIfPresent()` 等方法,参数为空时忽略条件
- Purpose: Redis Pub/Sub 消息监听抽象基类
- Examples: `hadoken-mq-spring-boot-starter/src/main/java/com/github/hadoken/framework/mq/core/pubsub/AbstractChannelMessageListener.java`
- Pattern: 实现 `onMessage()` 方法处理消息,自动注册到监听容器
- Purpose: MyBatis-Plus Mapper 基类扩展
- Examples: `hadoken-mybatis-spring-boot-starter/src/main/java/com/github/hadoken/framework/mybatis/core/mapper/BaseMapperPlus.java`
- Pattern: 继承 MyBatis-Plus BaseMapper,添加批量操作等增强方法
## Entry Points
- Location: `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- Triggers: Spring Boot 应用启动时自动扫描并加载
- Responsibilities: 注册各模块的核心 Bean
- Location: `hadoken-web-spring-boot-starter/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
- 注册 AutoConfiguration:
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
- **Threading:** 默认单线程事件循环,WebSocket 使用独立线程池 (`hadoken-websocket-spring-boot-starter/src/main/java/com/github/hadoken/framework/websocket/autoconfigure/HadokenWebSocketAutoConfiguration.java:97-104`)
- **Circular imports:** 无已知循环依赖,模块设计遵循单向依赖原则
## Anti-Patterns
### 空实现 Service
- 参考: `hadoken-web-spring-boot-starter/src/main/java/com/github/hadoken/framework/web/apilog/core/service/DefaultApiErrorLogService.java`
### 内存存储警告
- 参考: `hadoken-scheduler-spring-boot-starter/src/main/java/com/github/hadoken/framework/scheduler/autoconfigure/HadokenSchedulerAutoConfiguration.java:77`
## Error Handling
- **全局异常处理器:** `GlobalExceptionHandler` 使用 `@RestControllerAdvice` 拦截所有异常 (`hadoken-web-spring-boot-starter/src/main/java/com/github/hadoken/framework/web/mvc/core/handler/GlobalExceptionHandler.java`)
- **错误码枚举:** `GlobalErrorCodeConstants` 定义 HTTP 状态码对应的错误码 (`hadoken-common/src/main/java/com/github/hadoken/common/enums/GlobalErrorCodeConstants.java`)
- **业务异常:** `HadokenServiceException` 携带业务错误码抛出 (`hadoken-common/src/main/java/com/github/hadoken/common/exception/HadokenServiceException.java`)
- **错误日志:** 异常发生时调用 `ApiErrorLogService.createApiErrorLogAsync()` 异步记录
## Cross-Cutting Concerns
<!-- GSD:architecture-end -->

<!-- GSD:skills-start source:skills/ -->
## Project Skills

No project skills found. Add skills to any of: `.claude/skills/`, `.agents/skills/`, `.cursor/skills/`, `.github/skills/`, or `.codex/skills/` with a `SKILL.md` index file.
<!-- GSD:skills-end -->

<!-- GSD:workflow-start source:GSD defaults -->
## GSD Workflow Enforcement

Before using Edit, Write, or other file-changing tools, start work through a GSD command so planning artifacts and execution context stay in sync.

Use these entry points:
- `/gsd-quick` for small fixes, doc updates, and ad-hoc tasks
- `/gsd-debug` for investigation and bug fixing
- `/gsd-execute-phase` for planned phase work

Do not make direct repo edits outside a GSD workflow unless the user explicitly asks to bypass it.
<!-- GSD:workflow-end -->



<!-- GSD:profile-start -->
## Developer Profile

> Profile not yet configured. Run `/gsd-profile-user` to generate your developer profile.
> This section is managed by `generate-claude-profile` -- do not edit manually.
<!-- GSD:profile-end -->
