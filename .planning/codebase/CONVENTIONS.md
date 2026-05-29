# Coding Conventions

**Analysis Date:** 2026-05-29

## Naming Patterns

**Files:**
- Java files: PascalCase matching class name (e.g., `TaskManagerImpl.java`, `RedisUtils.java`)
- Package-info files: `package-info.java` for package-level documentation
- Configuration files: `application.yml`, `pom.xml`

**Classes:**
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

**Methods:**
- camelCase (e.g., `findById`, `createApiAccessLogAsync`, `getTaskOrThrow`)
- Boolean getters: `isXxx()` pattern (e.g., `isSuccess()`, `isError()`)
- Factory methods: `of`, `empty`, `error`, `success` (e.g., `CommonResult.success()`, `PageResult.empty()`)
- Async methods: `Async` suffix (e.g., `createApiAccessLogAsync`)

**Variables:**
- camelCase for local variables and fields
- Constants: UPPER_SNAKE_CASE (e.g., `MESSAGES`, `UNKNOWN`, `SEPARATOR`)
- Private fields: camelCase, typically with Lombok annotations

**Types:**
- Java 17 records for simple immutable data (e.g., `ErrorCode` as `record ErrorCode(Integer code, String msg)`)
- Lombok `@Data` for mutable entities
- Lombok `@Builder` for builder pattern

## Code Style

**Formatting:**
- UTF-8 encoding throughout
- Java 17 source/target
- No explicit checkstyle or formatter config files detected
- Standard Java indentation (4 spaces implied)

**Linting:**
- `@SuppressWarnings` annotations used selectively for:
  - `"unchecked"` - Generic type warnings
  - `"NullableProblems"` - Nullability warnings
  - `"SpringJavaInjectionPointsAutowiringInspection"` - Spring injection warnings

## Import Organization

**Order:**
1. Package declaration
2. Imports from same project (`com.github.hadoken.*`)
3. External library imports (Lombok, Spring, Jakarta, etc.)
4. Static imports (rarely used)

**Example from `HadokenWebSocketAutoConfiguration.java`:**
```java
package com.github.hadoken.framework.websocket.autoconfigure;

import com.github.hadoken.framework.websocket.core.interceptor.HadokenWebSocketHandleInterceptor;
import com.github.hadoken.framework.websocket.core.message.DefaultWebSocketSender;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import ...
```

**Path Aliases:**
- No path aliases detected
- Full package imports used throughout

## Error Handling

**Patterns:**
- Business exceptions: `HadokenServiceException` with `ErrorCode`
- ErrorCode uses Java record: `record ErrorCode(Integer code, String msg)`
- Error codes follow HTTP status conventions (400, 401, 403, 404, 500, etc.)
- Error message formatting uses `{}` placeholder (slf4j-style) via `HadokenServiceExceptionUtil`
- Global exception handler: `GlobalExceptionHandler` with `@RestControllerAdvice`

**Exception hierarchy:**
```java
// Core exception
public final class HadokenServiceException extends RuntimeException {
    private Integer code;
    private String message;
}

// Error codes
public record ErrorCode(Integer code, String msg) {}

// Utility for creating exceptions
HadokenServiceExceptionUtil.exception(ErrorCode errorCode, Object... params)
```

**Global handler patterns (from `GlobalExceptionHandler.java`):**
- `@ExceptionHandler(MissingServletRequestParameterException.class)` - parameter missing
- `@ExceptionHandler(MethodArgumentNotValidException.class)` - validation errors
- `@ExceptionHandler(ConstraintViolationException.class)` - constraint violations
- `@ExceptionHandler(HadokenServiceException.class)` - business exceptions
- `@ExceptionHandler(AccessDeniedException.class)` - permission denied
- `@ExceptionHandler(Exception.class)` - catch-all for system errors

## Logging

**Framework:** Slf4j via Lombok `@Slf4j` annotation

**Patterns:**
```java
@Slf4j
public class SomeClass {
    // Info level for important operations
    log.info("任务 '{}' 启动.", taskId);

    // Warn level for recoverable issues
    log.warn("[missingServletRequestParameterExceptionHandler]", ex);

    // Error level for system failures
    log.error("[defaultExceptionHandler]", ex);

    // Debug level for detailed tracing
    log.debug("成功删除缓存：{}, 结果: {}", key, result);
}
```

**Logging conventions:**
- Use Chinese for user-facing messages
- Include relevant context in log messages
- Exception parameter passed separately for stack trace: `log.error("message", ex)`
- Structured logging with placeholders: `log.info("操作完成: {}", value)`

## Comments

**When to Comment:**
- All public classes and interfaces require Javadoc
- All public methods require Javadoc
- Complex logic requires inline comments
- Package documentation via `package-info.java`

**Javadoc/TSDoc:**
```java
/**
 * 任务定义的规范化POJO (Plain Old Java Object)。
 * 这是框架内部用来表示一个任务完整定义的核心数据结构。
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2025/8/13 11:14
 */
@Data
@Builder
public class TaskDefinition { ... }
```

**Standard Javadoc header:**
- Description in Chinese
- `@author yanggj` (consistent author)
- `@version 1.0.0`
- `@date YYYY/MM/DD HH:mm`

**Inline comments:**
- Chinese for explanations
- English for technical terms
- Section separators with comment blocks: `// ========== Section Name ==========`

## Function Design

**Size:** Methods should be focused and reasonably sized

**Parameters:**
- Use `@Valid` for validation in service interfaces: `void createApiAccessLogAsync(@Valid ApiAccessLogDTO createDTO)`
- DTO objects for multiple parameters
- Optional via Java `Optional<T>` for nullable returns

**Return Values:**
- `CommonResult<T>` for API responses
- `Optional<T>` for nullable queries (e.g., `Optional<TaskDefinition> findById(String taskId)`)
- `List<T>` for collections, never null (empty list returned)
- Boolean methods use `isXxx()` pattern

**Example from `CommonResult.java`:**
```java
public static <T> CommonResult<T> success(T data) {
    CommonResult<T> result = new CommonResult<>();
    result.code = GlobalErrorCodeConstants.SUCCESS.code();
    result.data = data;
    result.msg = "";
    return result;
}

public static <T> CommonResult<T> error(ErrorCode errorCode) {
    return error(errorCode.code(), errorCode.msg());
}
```

## Module Design

**Exports:**
- Public interfaces for contracts
- Implementation classes in same package or sub-package
- Auto-configuration classes exposed via Spring Boot mechanism

**Barrel Files:**
- `package-info.java` for package documentation
- No explicit barrel/index files

**Spring Boot Starter Structure:**
```
hadoken-xxx-spring-boot-starter/
├── src/main/java/com/github/hadoken/framework/xxx/
│   ├── autoconfigure/
│   │   ├── XxxAutoConfiguration.java
│   │   └── XxxProperties.java
│   ├── core/
│   │   ├── handler/
│   │   ├── interceptor/
│   │   ├── service/
│   │   └── model/
│   └── util/
├── src/main/resources/
│   └── META-INF/spring/
│       └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

## Lombok Usage

**Core annotations:**
- `@Data` - getters, setters, toString, equals, hashCode
- `@Slf4j` - logger field
- `@Builder` - builder pattern
- `@NoArgsConstructor` / `@AllArgsConstructor` - constructor generation
- `@Getter` / `@Setter` - selective property accessors
- `@SneakyThrows` - exception wrapping

**Example from `TaskDefinition.java`:**
```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDefinition {
    private String id;
    private String description;
    private TaskSourceType sourceType;
    ...
}
```

## Dependency Management

**Scope conventions:**
- `provided` - used only by utilities, not packaged (e.g., `spring-core`, `spring-expression`)
- `optional` - annotation processors (e.g., `spring-boot-configuration-processor`)
- `compile` - core dependencies
- `test` - test-only dependencies

**Version management:**
- All versions in `hadoken-dependencies/pom.xml` BOM
- Properties for each version: `<redisson.version>3.45.1</redisson.version>`
- Import via `<scope>import</scope>` in dependencyManagement

---

*Convention analysis: 2026-05-29*