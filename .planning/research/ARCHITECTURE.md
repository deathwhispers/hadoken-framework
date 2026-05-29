# Architecture Impact Research: JDK 25 + Spring Boot Upgrade

**Project:** Hadoken Framework
**Research Focus:** Architecture changes required for JDK 25 + Spring Boot upgrade
**Researched:** 2026-05-29
**Confidence:** MEDIUM (网络访问受限，部分信息需要验证)

---

## Executive Summary

本研究分析从 JDK 17 + Spring Boot 3.3.13 升级到 JDK 25 + Spring Boot 3.5.x 对多模块 Maven 架构的影响。

**核心发现:**
- 多模块架构无需结构性变更
- AutoConfiguration 机制保持兼容
- JPMS 模块系统为可选增强
- 构建配置需要更新 JDK 版本

---

## Current Architecture Analysis (当前架构分析)

### Module Structure

```
hadoken-framework/
├── hadoken-dependencies/          # BOM (Bill of Materials)
├── hadoken-common/                # 公共基础模块
├── hadoken-web-spring-boot-starter/
├── hadoken-security-spring-boot-starter/
├── hadoken-mybatis-spring-boot-starter/
├── hadoken-redis-spring-boot-starter/
├── hadoken-mq-spring-boot-starter/
├── hadoken-scheduler-spring-boot-starter/
├── hadoken-websocket-spring-boot-starter/
├── hadoken-monitor-spring-boot-starter/
├── hadoken-mqtt-spring-boot-starter/
├── hadoken-stats-spring-boot-starter/
├── hadoken-jpa-spring-boot-starter/
└── hadoken-test/                  # 测试模块
```

**依赖关系:**
- 所有 Starter 模块依赖 `hadoken-common`
- 所有模块通过 `hadoken-dependencies` BOM 管理版本
- 模块之间无相互依赖（单向依赖设计）

### AutoConfiguration Pattern

当前使用 Spring Boot 3.x 的自动配置机制:

```
META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

示例内容（web 模块）:
```
com.github.hadoken.framework.web.apilog.autoconfigure.HadokenApiLogAutoConfiguration
com.github.hadoken.framework.web.encrypt.autoconfigure.HadokenApiEncryptAutoConfiguration
com.github.hadoken.framework.web.jackson.autoconfigure.HadokenJacksonAutoConfiguration
com.github.hadoken.framework.web.mvc.autoconfigure.HadokenWebAutoConfiguration
com.github.hadoken.framework.web.springdoc.autoconfigure.HadokenSwaggerAutoConfiguration
com.github.hadoken.framework.web.xss.autoconfigure.HadokenXssAutoConfiguration
```

**AutoConfiguration 实现模式:**

```java
@Configuration
@EnableConfigurationProperties({WebProperties.class})
public class HadokenWebAutoConfiguration implements WebMvcConfigurer {
    // 使用 @ConditionalOnProperty 条件装配
    // 使用 @ConditionalOnMissingBean 允许覆盖
    // 使用 @Bean 注册组件
}
```

---

## Impact Analysis (影响分析)

### 1. Maven Multi-Module Compatibility

**结论: 无架构变更需求**

Maven 多模块架构与 JDK 25 完全兼容:

| 方面 | JDK 17 | JDK 25 | 影响 |
|------|--------|--------|------|
| POM 结构 | 相同 | 相同 | 无变更 |
| 模块依赖 | 单向依赖 | 单向依赖 | 无变更 |
| BOM 管理 | 支持 | 支持 | 无变更 |
| 构建生命周期 | 标准 | 标准 | 无变更 |

**需要的变更:**

```xml
<!-- 根 pom.xml 和 hadoken-dependencies/pom.xml -->
<properties>
    <java.version>25</java.version>
    <maven-compiler-plugin.version>3.14.0</maven-compiler-plugin.version>
</properties>
```

**Maven 版本要求:**

| Maven 版本 | JDK 25 支持 | 推荐 |
|------------|-------------|------|
| Maven 3.6.x | 部分 | 不推荐 |
| Maven 3.8.x | 支持 | 可用 |
| Maven 3.9.x | 完全支持 | 推荐 |

### 2. Spring Boot AutoConfiguration Changes

**结论: API 稳定，无破坏性变更**

Spring Boot 3.3.x → 3.5.x 自动配置机制变化:

| 特性 | Spring Boot 3.3.x | Spring Boot 3.5.x | 影响 |
|------|-------------------|-------------------|------|
| `.imports` 文件 | 支持 | 支持 | 无变更 |
| `@AutoConfiguration` | 支持 | 支持 | 无变更 |
| `@EnableConfigurationProperties` | 支持 | 支持 | 无变更 |
| `@ConditionalOnXxx` | 支持 | 支持 | 无变更 |
| `@Configuration` | 支持 | 支持 | 无变更 |

**可能的新特性 (需验证):**

1. **AutoConfiguration 订序增强**
   - Spring Boot 3.4+ 引入更精细的自动配置排序
   - 可能需要检查 `@AutoConfigureBefore` / `@AutoConfigureAfter`

2. **条件注解增强**
   - 新的条件注解可能在 3.5.x 中引入
   - 当前代码可继续使用，无需修改

**建议:**

```java
// 当前模式保持不变
@Configuration
@EnableConfigurationProperties({WebProperties.class})
public class HadokenWebAutoConfiguration implements WebMvcConfigurer {
    // 无需修改
}
```

### 3. JPMS (Java Platform Module System) Considerations

**结论: 可选增强，非必需**

JDK 25 完全支持 JPMS，但 Spring Boot 应用通常不使用:

| 考量 | 采用 JPMS | 不采用 JPMS |
|------|-----------|-------------|
| 模块边界 | 编译时强制 | 运行时松散 |
| 类路径隔离 | 强隔离 | 弱隔离 |
| 启动性能 | 可能提升 | 无变化 |
| 维护成本 | 高 (需要 module-info.java) | 无 |
| 第三方库兼容 | 部分不支持 | 全部兼容 |

**建议: 暂不引入 JPMS**

理由:
1. Spring Boot 生态对 JPMS 支持有限
2. 第三方依赖（Hutool、MyBatis-Plus 等）可能不支持
3. 自动配置机制依赖类路径扫描，与 JPMS 冲突
4. 维护成本高，收益有限

**未来可选方案 (如果需要 JPMS):**

```java
// module-info.java (每个模块)
module hadoken.common {
    requires transitive spring.core;
    requires transitive spring.beans;
    requires transitive jackson.annotations;
    requires static lombok;
    
    exports com.github.hadoken.common.result;
    exports com.github.hadoken.common.exception;
    exports com.github.hadoken.common.enums;
    // ...
}
```

**阻碍因素:**

1. Hutool 不支持 JPMS（无 Automatic-Module-Name）
2. MyBatis-Plus 未完全模块化
3. Spring Boot 自动配置依赖类路径扫描
4. 部分依赖使用反射，与模块系统冲突

### 4. Build and Deployment Changes

**Maven 构建配置变更:**

```xml
<!-- pom.xml 构建配置 -->
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.14.0</version>
            <configuration>
                <release>25</release>
                <compilerArgs>
                    <arg>-parameters</arg>
                    <!-- 可选: 启用预览特性 -->
                    <!-- <arg>--enable-preview</arg> -->
                </compilerArgs>
            </configuration>
        </plugin>
        <!-- 新增: 支持模块路径 (如果采用 JPMS) -->
        <!--
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <configuration>
                <archive>
                    <manifestEntries>
                        <Automatic-Module-Name>com.github.hadoken.common</Automatic-Module-Name>
                    </manifestEntries>
                </archive>
            </configuration>
        </plugin>
        -->
    </plugins>
</build>
```

**部署变更:**

| 部署方式 | JDK 17 | JDK 25 | 变更 |
|----------|--------|--------|------|
| JAR 部署 | 支持 | 支持 | 无变更 |
| Docker 部署 | JDK 17 镜像 | JDK 25 镜像 | 更新基础镜像 |
| Kubernetes | 运行时 JDK | 运行时 JDK | 更新镜像版本 |

**Dockerfile 变更:**

```dockerfile
# 当前
FROM eclipse-temurin:17-jre

# 升级后
FROM eclipse-temurin:25-jre
# 或
FROM amazoncorretto:25-alpine
```

### 5. Dependency Management Changes

**BOM 结构无变更:**

```xml
<!-- hadoken-dependencies/pom.xml 结构保持不变 -->
<project>
    <artifactId>hadoken-dependencies</artifactId>
    <packaging>pom</packaging>
    
    <dependencyManagement>
        <dependencies>
            <!-- Spring Boot BOM -->
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-dependencies</artifactId>
                <version>${spring-boot.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
            <!-- 其他依赖... -->
        </dependencies>
    </dependencyManagement>
</project>
```

**版本属性更新:**

```xml
<properties>
    <!-- 核心框架版本升级 -->
    <java.version>25</java.version>
    <spring-boot.version>3.5.x</spring-boot.version>
    <spring-cloud.version>2025.0.x</spring-cloud.version>
    <spring-cloud-alibaba.version>2023.0.3.x</spring-cloud-alibaba.version>
    
    <!-- 依赖版本可能需要更新 -->
    <maven-compiler-plugin.version>3.14.0</maven-compiler-plugin.version>
</properties>
```

---

## Module-Specific Impact (模块特定影响)

### hadoken-common (公共基础模块)

**当前结构:**
```
hadoken-common/
├── src/main/java/com/github/hadoken/common/
│   ├── result/          # CommonResult 等
│   ├── exception/       # HadokenServiceException
│   ├── enums/           # 枚举定义
│   ├── util/            # 工具类
│   ├── entity/          # 实体类
│   └── core/            # 核心接口
```

**JDK 25 新特性可利用:**

| 特性 | 应用场景 | 示例 |
|------|----------|------|
| Record Patterns | 模式匹配 | `if (obj instanceof CommonResult(Integer code, String msg))` |
| Pattern Matching for switch | 异常处理 | 根据 ErrorCode 类型匹配处理 |
| Virtual Threads | 异步工具 | 异步日志、异步任务 |
| String Templates | SQL 构建 | `STR."SELECT * FROM \{table}"` |
| Scoped Values | 替代 ThreadLocal | TraceId、SecurityContext |

**建议重构:**

```java
// 当前
public class CommonResult<T> implements Serializable {
    private Integer code;
    private T data;
    private String msg;
    // getters, setters, builders...
}

// 可选: 使用 Record (不可变响应)
public record CommonResult<T>(Integer code, T data, String msg) {
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(0, data, "");
    }
    
    public static <T> CommonResult<T> error(Integer code, String msg) {
        return new CommonResult<>(code, null, msg);
    }
}

// 注意: Record 不兼容 Jackson 序列化的某些场景，需要评估
```

### hadoken-web-spring-boot-starter

**当前过滤器链:**

```
HTTP Request
    ↓
TraceFilter (monitor)
    ↓
CacheRequestBodyFilter (web)
    ↓
XssFilter (web)
    ↓
ApiAccessLogFilter (web)
    ↓
CorsFilter (web)
    ↓
Controller
```

**JDK 25 优化点:**

1. **Virtual Threads 支持**

```java
// 当前
@Bean
public FilterRegistrationBean<TracerFilter> tracerFilter() {
    // ...
}

// 可选: 虚拟线程执行
@Bean
public FilterRegistrationBean<TracerFilter> tracerFilter() {
    FilterRegistrationBean<TracerFilter> bean = new FilterRegistrationBean<>();
    bean.setFilter(new TracerFilter());
    // 启用虚拟线程 (Spring Boot 3.2+)
    bean.setAsyncSupported(true);
    return bean;
}
```

2. **结构化并发**

```java
// 当前异步处理
@Async
public void createApiErrorLogAsync(ApiErrorLogDO log) {
    apiErrorLogMapper.insert(log);
}

// JDK 25 结构化并发
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    StructuredTaskScope.Subtask<Void> task = scope.fork(() -> {
        apiErrorLogMapper.insert(log);
        return null;
    });
    scope.join();
    scope.throwIfFailed();
}
```

### hadoken-mybatis-spring-boot-starter

**当前条件查询扩展:**

```java
// QueryWrapperX - 条件查询扩展
public class QueryWrapperX<T> extends QueryWrapper<T> {
    public QueryWrapperX<T> eqIfPresent(String column, Object val) {
        if (val != null) {
            eq(column, val);
        }
        return this;
    }
}
```

**JDK 25 模式匹配优化:**

```java
// 可选: 使用模式匹配简化
public QueryWrapperX<T> eqIfPresent(String column, Object val) {
    return switch (val) {
        case null -> this;
        case String s when s.isEmpty() -> this;
        default -> eq(column, val);
    };
}
```

### hadoken-redis-spring-boot-starter

**当前序列化:**

```java
// Jackson 序列化
public class JsonRedisSerializer<T> implements RedisSerializer<T> {
    // ...
}
```

**JDK 25 优化:**

```java
// 可选: 使用 String Templates 构建 Key
public String buildKey(String prefix, String suffix) {
    return STR."\{prefix}:\{suffix}";
}
```

### hadoken-scheduler-spring-boot-starter

**当前任务调度:**

```java
// ThreadPoolTaskScheduler
@Bean
public TaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(10);
    return scheduler;
}
```

**JDK 25 Virtual Threads 优化:**

```java
// 使用虚拟线程执行任务
@Bean
public TaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(10);
    // Spring Boot 3.2+ 支持虚拟线程
    scheduler.setThreadFactory(Thread.ofVirtual().factory());
    return scheduler;
}

// 或使用配置属性
spring.threads.virtual.enabled=true
```

---

## Patterns to Follow (遵循的模式)

### Pattern 1: 条件装配

**当前模式 (保持):**

```java
@Configuration
@EnableConfigurationProperties({SchedulerProperties.class})
public class HadokenSchedulerAutoConfiguration {
    
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "hadoken.scheduler", name = "enabled", havingValue = "true", matchIfMissing = true)
    public TaskManager taskManager(SchedulerProperties properties) {
        return new TaskManagerImpl(properties);
    }
}
```

### Pattern 2: 配置属性

**当前模式 (保持):**

```java
@ConfigurationProperties(prefix = "hadoken.web")
public class WebProperties {
    private boolean enabled = true;
    private CorsProperties cors = new CorsProperties();
    // ...
}
```

### Pattern 3: 统一异常处理

**当前模式 (保持):**

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(HadokenServiceException.class)
    public CommonResult<?> handleHadokenServiceException(HadokenServiceException e) {
        return CommonResult.error(e.getCode(), e.getMessage());
    }
}
```

---

## Anti-Patterns to Avoid (避免的反模式)

### Anti-Pattern 1: 直接使用 JDK 25 预览特性

**问题:** 预览特性在后续版本可能变更或移除

**正确做法:** 仅使用正式特性 (GA)

```xml
<!-- 不要在 pom.xml 中启用预览特性 -->
<!-- 错误示例 -->
<compilerArgs>
    <arg>--enable-preview</arg>
</compilerArgs>
```

### Anti-Pattern 2: 盲目升级所有依赖

**问题:** 可能引入不兼容版本

**正确做法:** 按优先级分批升级

```
1. 核心框架 (Spring Boot, Spring Cloud)
2. 数据层 (MyBatis-Plus, Druid, MySQL)
3. 工具层 (Hutool, Guava)
4. 文档层 (SpringDoc, Knife4j)
5. 监控层 (SkyWalking, Admin)
```

### Anti-Pattern 3: 忽略 ThreadLocal 迁移

**问题:** JDK 25 引入 Scoped Values，但 ThreadLocal 仍然有效

**正确做法:** 评估 Scoped Values 适用场景

```java
// 当前 ThreadLocal 模式 (保持)
private static final ThreadLocal<String> TRACE_ID = new TransmittableThreadLocal<>();

// 可选: Scoped Values (如果需要)
private static final ScopedValue<String> TRACE_ID = ScopedValue.newInstance();
```

---

## Scalability Considerations (可扩展性考量)

| 场景 | 当前方案 | JDK 25 优化 | 建议 |
|------|----------|-------------|------|
| 高并发请求 | 线程池 | 虚拟线程 | 启用虚拟线程 |
| 异步任务 | @Async | 结构化并发 | 评估迁移 |
| 连接池 | HikariCP | 保持 | 无变更 |
| 缓存 | Redis | 保持 | 无变更 |

---

## Migration Checklist (迁移检查清单)

### Phase 1: 准备阶段

- [ ] 更新 Maven 到 3.9.x
- [ ] 更新 IDE 到支持 JDK 25 的版本
- [ ] 更新 CI/CD 环境的 JDK 版本
- [ ] 备份当前代码

### Phase 2: 构建配置

- [ ] 更新 `java.version` 到 25
- [ ] 更新 `maven-compiler-plugin` 到 3.14.0
- [ ] 验证编译通过

### Phase 3: 依赖升级

- [ ] 更新 Spring Boot 版本
- [ ] 更新 Spring Cloud 版本
- [ ] 更新 Spring Cloud Alibaba 版本
- [ ] 更新第三方依赖版本

### Phase 4: 代码适配

- [ ] 检查废弃 API 使用
- [ ] 更新自动配置（如有变更）
- [ ] 验证所有模块功能

### Phase 5: 测试验证

- [ ] 运行单元测试
- [ ] 运行集成测试
- [ ] 性能基准测试
- [ ] 安全扫描

---

## Risk Assessment (风险评估)

### 高风险

| 风险 | 影响 | 概率 | 缓解措施 |
|------|------|------|----------|
| Spring Cloud Alibaba 版本不兼容 | 微服务功能失效 | 中 | 等待官方版本发布 |
| 第三方依赖不支持 JDK 25 | 编译/运行错误 | 低 | 提前验证依赖兼容性 |

### 中风险

| 风险 | 影响 | 概率 | 缓解措施 |
|------|------|------|----------|
| 自动配置行为变更 | 功能异常 | 低 | 测试所有模块 |
| 性能回归 | 响应时间增加 | 低 | 性能基准测试 |

### 低风险

| 风险 | 影响 | 概率 | 缓解措施 |
|------|------|------|----------|
| IDE 兼容性 | 开发效率降低 | 低 | 更新 IDE 版本 |
| 构建工具兼容性 | 构建失败 | 低 | 更新 Maven 版本 |

---

## Recommended Architecture Changes (推荐架构变更)

### 必须变更

| 变更 | 位置 | 内容 |
|------|------|------|
| JDK 版本 | pom.xml | `<java.version>25</java.version>` |
| Maven Compiler | pom.xml | `<maven-compiler-plugin.version>3.14.0</maven-compiler-plugin.version>` |
| Spring Boot | hadoken-dependencies/pom.xml | `<spring-boot.version>3.5.x</spring-boot.version>` |
| 基础镜像 | Dockerfile | `FROM eclipse-temurin:25-jre` |

### 可选变更

| 变更 | 位置 | 内容 | 优先级 |
|------|------|------|--------|
| Virtual Threads | application.yml | `spring.threads.virtual.enabled=true` | 高 |
| 结构化并发 | hadoken-common | 异步任务重构 | 中 |
| Scoped Values | hadoken-common | 替换部分 ThreadLocal | 低 |
| Record 重构 | hadoken-common | 不可变 DTO | 低 |

### 不推荐变更

| 变更 | 原因 |
|------|------|
| JPMS 模块化 | 生态不成熟，维护成本高 |
| 预览特性 | 稳定性风险 |
| 全面 Record 重构 | 序列化兼容性问题 |

---

## Verification Commands (验证命令)

```bash
# 1. 检查 JDK 版本
java -version
# 期望: openjdk version "25.x.x"

# 2. 检查 Maven 版本
mvn -version
# 期望: Apache Maven 3.9.x

# 3. 编译所有模块
mvn clean compile -DskipTests

# 4. 运行测试
mvn test

# 5. 检查依赖树
mvn dependency:tree

# 6. 打包
mvn package -DskipTests

# 7. 检查自动配置
mvn spring-boot:run -Dspring-boot.run.arguments="--debug"
```

---

## Build Order Implications (构建顺序影响)

**当前构建顺序 (不变):**

```
1. hadoken-dependencies (BOM)
2. hadoken-common (基础模块)
3. hadoken-*-spring-boot-starter (各 Starter)
4. hadoken-test (测试模块)
```

**Maven Reactor 自动处理依赖顺序，无需手动调整。**

---

## Sources

**置信度说明:**
- HIGH: 官方文档确认或实际代码分析
- MEDIUM: 基于训练数据推断，需验证
- LOW: 推测，必须验证

**参考来源:**

1. **项目分析 (HIGH)**
   - `.planning/codebase/ARCHITECTURE.md`
   - `.planning/codebase/STRUCTURE.md`
   - `pom.xml`
   - `hadoken-dependencies/pom.xml`

2. **Spring Boot 文档 (需验证)**
   - https://docs.spring.io/spring-boot/docs/3.5.x/reference/html/
   - https://github.com/spring-projects/spring-boot/wiki

3. **JDK 文档 (需验证)**
   - https://openjdk.org/projects/jdk/25/
   - https://openjdk.org/jeps/

---

*Research completed: 2026-05-29*
*Note: 网络访问受限，部分版本和特性信息需要手动验证*