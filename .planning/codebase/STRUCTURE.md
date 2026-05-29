# Codebase Structure

**Analysis Date:** 2026-05-29

## Directory Layout

```
hadoken-framework/                     # 项目根目录
├── hadoken-common/                    # 公共基础模块
├── hadoken-dependencies/              # BOM依赖管理
├── hadoken-web-spring-boot-starter/   # Web增强模块
├── hadoken-security-spring-boot-starter/ # Security增强模块
├── hadoken-mybatis-spring-boot-starter/ # MyBatis-Plus增强模块
├── hadoken-redis-spring-boot-starter/ # Redis增强模块
├── hadoken-mq-spring-boot-starter/    # 消息队列模块
├── hadoken-scheduler-spring-boot-starter/ # 任务调度模块
├── hadoken-websocket-spring-boot-starter/ # WebSocket模块
├── hadoken-monitor-spring-boot-starter/ # 监控追踪模块
├── hadoken-mqtt-spring-boot-starter/  # MQTT集成模块
├── hadoken-stats-spring-boot-starter/ # 统计分析模块
├── hadoken-jpa-spring-boot-starter/   # JPA增强模块
├── hadoken-test/                      # 测试模块
├── pom.xml                            # 根POM文件
└── .planning/                         # 规划文档目录
```

## Directory Purposes

**hadoken-common:**
- Purpose: 公共基础组件,被所有模块依赖
- Contains: 统一响应、异常、枚举、工具类、实体类
- Key files:
  - `src/main/java/com/github/hadoken/common/result/CommonResult.java` - 统一响应封装
  - `src/main/java/com/github/hadoken/common/exception/HadokenServiceException.java` - 业务异常
  - `src/main/java/com/github/hadoken/common/enums/GlobalErrorCodeConstants.java` - 全局错误码
  - `src/main/java/com/github/hadoken/common/util/` - 工具类集合

**hadoken-dependencies:**
- Purpose: 统一依赖版本管理(BOM)
- Contains: 所有第三方依赖版本定义
- Key files: `pom.xml` - 定义 Spring Boot、Spring Cloud、数据库、中间件等所有版本

**hadoken-web-spring-boot-starter:**
- Purpose: Web层功能增强
- Contains: 全局异常处理、CORS配置、XSS防护、API日志、数据脱敏、加解密、Swagger配置
- Key directories:
  - `src/main/java/.../mvc/` - MVC核心(Web配置、异常处理、过滤器)
  - `src/main/java/.../apilog/` - API访问日志
  - `src/main/java/.../xss/` - XSS防护
  - `src/main/java/.../desensitize/` - 数据脱敏
  - `src/main/java/.../encrypt/` - API加解密
  - `src/main/java/.../jackson/` - Jackson配置
  - `src/main/java/.../springdoc/` - Swagger/Knife4j

**hadoken-security-spring-boot-starter:**
- Purpose: Spring Security功能增强
- Contains: 安全配置、认证切面、安全上下文策略、权限处理
- Key directories:
  - `src/main/java/.../autoconfigure/` - 自动配置
  - `src/main/java/.../core/` - Security核心组件(LoginUser、切面、工具)

**hadoken-mybatis-spring-boot-starter:**
- Purpose: MyBatis-Plus功能增强
- Contains: 数据源配置、Mapper扫描、字段填充、加密解密、查询扩展
- Key directories:
  - `src/main/java/.../datasource/` - 数据源配置
  - `src/main/java/.../mybatis/core/condition/` - 条件查询扩展(QueryWrapperX等)
  - `src/main/java/.../mybatis/core/codec/` - 字段加密解密
  - `src/main/java/.../mybatis/core/mapper/` - Mapper基类
  - `src/main/java/.../mybatis/core/entity/` - 实体基类

**hadoken-redis-spring-boot-starter:**
- Purpose: Redis功能增强
- Contains: 缓存管理、序列化配置、工具类封装
- Key directories:
  - `src/main/java/.../autoconfigure/` - Redis自动配置
  - `src/main/java/.../core/` - Redis核心(Key定义、注册)
  - `src/main/java/.../util/RedisUtils.java` - Redis工具类

**hadoken-mq-spring-boot-starter:**
- Purpose: Redis消息队列封装
- Contains: 消息模板、监听器基类、拦截器
- Key directories:
  - `src/main/java/.../core/RedisMQTemplate.java` - 消息发送模板
  - `src/main/java/.../core/pubsub/` - Pub/Sub监听器
  - `src/main/java/.../core/stream/` - Stream消息监听
  - `src/main/java/.../core/message/` - 消息模型基类
  - `src/main/java/.../core/interceptor/` - 消息拦截器

**hadoken-scheduler-spring-boot-starter:**
- Purpose: 轻量级任务调度
- Contains: 任务管理、多种存储实现、管理端点
- Key directories:
  - `src/main/java/.../manager/` - 任务管理器
  - `src/main/java/.../store/` - 存储实现(MyBatis/Redis/Memory)
  - `src/main/java/.../endpoint/` - 管理端点Controller
  - `src/main/java/.../model/` - 任务模型

**hadoken-websocket-spring-boot-starter:**
- Purpose: WebSocket(STOMP)增强
- Contains: STOMP配置、消息发送器、拦截器
- Key directories:
  - `src/main/java/.../autoconfigure/` - WebSocket自动配置
  - `src/main/java/.../core/message/` - 消息发送器
  - `src/main/java/.../core/interceptor/` - WebSocket拦截器

**hadoken-monitor-spring-boot-starter:**
- Purpose: 监控追踪(SkyWalking)
- Contains: TraceId过滤器、业务追踪切面
- Key directories:
  - `src/main/java/.../core/filter/TracerFilter.java` - TraceId设置
  - `src/main/java/.../core/aop/BizTracerAspect.java` - 业务追踪切面
  - `src/main/java/.../core/annotation/BizTracer.java` - 业务追踪注解

**hadoken-mqtt-spring-boot-starter:**
- Purpose: MQTT客户端集成
- Contains: Eclipse Paho客户端配置、连接管理
- Key directories: `src/main/java/.../autoconfigure/`

**hadoken-stats-spring-boot-starter:**
- Purpose: 统计分析工具
- Contains: 时间段解析器、同比环比计算、时序模型
- Key directories:
  - `src/main/java/.../period/resolver/` - 时间段解析器
  - `src/main/java/.../period/model/` - 统计模型
  - `src/main/java/.../period/util/` - 时间工具

**hadoken-jpa-spring-boot-starter:**
- Purpose: JPA功能增强
- Contains: 基础实体、DTO、查询辅助、数据权限
- Key directories:
  - `src/main/java/.../base/` - 基础类(BaseEntity、BaseDTO、BaseMapper)
  - `src/main/java/.../annotation/` - 查询注解、数据权限注解
  - `src/main/java/.../config/` - JPA配置

**hadoken-test:**
- Purpose: 测试模块
- Contains: 调度器测试、配置示例
- Key files: `src/main/java/.../ScheduleTest.java`

## Key File Locations

**Entry Points:**
- `pom.xml` - Maven 根配置
- `hadoken-dependencies/pom.xml` - 依赖版本管理
- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` - 各模块自动装配入口

**Configuration:**
- `hadoken-web-spring-boot-starter/src/main/java/.../mvc/autoconfigure/WebProperties.java` - Web配置属性
- `hadoken-redis-spring-boot-starter/src/main/java/.../autoconfigure/HadokenRedisProperties.java` - Redis配置属性
- `hadoken-scheduler-spring-boot-starter/src/main/java/.../autoconfigure/HadokenSchedulerProperties.java` - 调度器配置属性
- `hadoken-websocket-spring-boot-starter/src/main/java/.../autoconfigure/WebSocketProperties.java` - WebSocket配置属性

**Core Logic:**
- `hadoken-common/src/main/java/com/github/hadoken/common/result/CommonResult.java` - 统一响应
- `hadoken-common/src/main/java/com/github/hadoken/common/exception/HadokenServiceException.java` - 业务异常
- `hadoken-web-spring-boot-starter/src/main/java/.../mvc/core/handler/GlobalExceptionHandler.java` - 全局异常处理
- `hadoken-mybatis-spring-boot-starter/src/main/java/.../mybatis/core/condition/query/QueryWrapperX.java` - 条件查询扩展

**Testing:**
- `hadoken-test/src/main/java/com/github/hadoken/framework/test/ScheduleTest.java` - 调度器测试

## Naming Conventions

**模块命名:**
- Pattern: `hadoken-{功能}-spring-boot-starter`
- Examples: `hadoken-web-spring-boot-starter`, `hadoken-redis-spring-boot-starter`

**包命名:**
- Pattern: `com.github.hadoken.{模块}.{子功能}.{类型}`
- 示例: `com.github.hadoken.framework.web.mvc.autoconfigure`

**类命名:**
- AutoConfiguration: `Hadoken{功能}AutoConfiguration` (如 `HadokenWebAutoConfiguration`)
- Properties: `{功能}Properties` 或 `Hadoken{功能}Properties` (如 `WebProperties`, `HadokenRedisProperties`)
- 工具类: `{功能}Utils` (如 `RedisUtils`, `JsonUtils`)
- 过滤器: `{功能}Filter` (如 `TracerFilter`, `XssFilter`)
- 处理器: `{功能}Handler` (如 `GlobalExceptionHandler`)
- 基类: `Base{类型}` 或 `Abstract{类型}` (如 `BaseDO`, `AbstractChannelMessageListener`)
- 扩展类: `{类名}X` (如 `QueryWrapperX`, `LambdaQueryWrapperX`)

**配置属性前缀:**
- Pattern: `hadoken.{模块}.{属性}`
- Examples: `hadoken.web.cors`, `hadoken.scheduler.store.type`, `hadoken.redis.key-ttl`

**文件命名:**
- Java: PascalCase (类名与文件名一致)
- 配置: `application.yml` 或 `application-{profile}.yml`
- 资源: `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

## Where to Add New Code

**新增 Starter 模块:**
1. 创建目录: `hadoken-{功能}-spring-boot-starter/`
2. 创建 pom.xml,依赖 `hadoken-common` 和 `hadoken-dependencies`
3. 创建 AutoConfiguration 类: `src/main/java/.../autoconfigure/Hadoken{功能}AutoConfiguration.java`
4. 创建 Properties 类: `src/main/java/.../autoconfigure/{功能}Properties.java`
5. 创建自动装配文件: `src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
6. 在根 pom.xml 添加 `<module>`
7. 在 hadoken-dependencies/pom.xml 添加内部依赖版本

**新增 Web 功能:**
- 过滤器: `hadoken-web-spring-boot-starter/src/main/java/.../mvc/core/filter/`
- 处理器: `hadoken-web-spring-boot-starter/src/main/java/.../mvc/core/handler/`
- 配置: `hadoken-web-spring-boot-starter/src/main/java/.../mvc/autoconfigure/`

**新增 MyBatis 增强:**
- 条件扩展: `hadoken-mybatis-spring-boot-starter/src/main/java/.../mybatis/core/condition/`
- 字段处理: `hadoken-mybatis-spring-boot-starter/src/main/java/.../mybatis/core/handler/`
- 注解: `hadoken-mybatis-spring-boot-starter/src/main/java/.../mybatis/core/annotation/`

**新增工具类:**
- 公共工具: `hadoken-common/src/main/java/com/github/hadoken/common/util/`

**新增枚举:**
- `hadoken-common/src/main/java/com/github/hadoken/common/enums/`

**新增异常:**
- `hadoken-common/src/main/java/com/github/hadoken/common/exception/`

**新增测试:**
- `hadoken-test/src/main/java/com/github/hadoken/framework/test/`

## Special Directories

**target/ (各模块):**
- Purpose: Maven 构建输出目录
- Generated: Yes (编译产物)
- Committed: No (.gitignore 忽略)

**.planning/ (项目根):**
- Purpose: 项目规划文档存储
- Generated: No (手动维护)
- Committed: Yes (规划文档应提交)

**META-INF/spring/ (各模块):**
- Purpose: Spring Boot 自动装配配置
- Generated: No
- Committed: Yes

## Package Structure Per Module

每个 Starter 模块遵循以下包结构:

```
src/main/java/com/github/hadoken/framework/{模块}/
├── autoconfigure/       # 自动配置类
│   ├── {功能}AutoConfiguration.java
│   ├── {功能}Properties.java
├── core/                # 核心实现
│   ├── filter/          # 过滤器(如有)
│   ├── handler/         # 处理器(如有)
│   ├── interceptor/     # 拦截器(如有)
│   ├── aop/             # 切面(如有)
│   ├── annotation/      # 注解(如有)
│   ├── util/            # 工具类(如有)
│   ├── message/         # 消息模型(如有)
│   ├── model/           # 数据模型(如有)
│   ├── enums/           # 枚举(如有)
│   ├── mapper/          # Mapper(如有)
│   ├── entity/          # 实体(如有)
├── endpoint/            # 端点Controller(如有)
├── manager/             # 管理器(如有)
├── store/               # 存储实现(如有)

src/main/resources/
├── META-INF/spring/
│   └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

## Import Organization

模块导入顺序:
1. Java 标准库 (`java.*`)
2. Jakarta EE (`jakarta.*`)
3. Spring Framework (`org.springframework.*`)
4. 第三方库 (`cn.hutool.*`, `com.baomidou.*`, `org.apache.*`)
5. 内部模块 (`com.github.hadoken.common.*`, `com.github.hadoken.framework.*`)

---

*Structure analysis: 2026-05-29*