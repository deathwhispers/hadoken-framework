# JDK 25 代码规范

## 概述

本文档为 Hadoken Framework 项目制定 JDK 25 新特性代码规范，确保开发团队在使用 JDK 25 新特性时有明确的指导原则。本规范基于 JDK 25 正式特性，不启用 Preview Features，强调稳定性优先。

**核心原则：**
- 仅使用 JDK 25 正式发布的稳定特性
- 不启用任何预览特性（Preview Features）
- 为每个新特性提供具体的使用场景和代码示例
- 强调正确使用场景，避免误用

## 目录

1. [Virtual Threads](#virtual-threads)
2. [Pattern Matching](#pattern-matching)
3. [Scoped Values](#scoped-values)
4. [Value Types](#value-types)
5. [最佳实践](#最佳实践)
6. [参考资源](#参考资源)

## Virtual Threads

### 概念说明

虚拟线程（Virtual Threads）是 JDK 21 引入的轻量级线程，在 JDK 25 中已成为正式特性。与传统平台线程（Platform Threads）相比，虚拟线程具有以下特点：

- **轻量级**：内存占用小，可创建数百万个虚拟线程
- **I/O 密集型优化**：在 I/O 等待时自动挂起，释放线程资源
- **自动调度**：由 JVM 调度到平台线程上执行
- **简化并发编程**：使用同步代码风格编写异步程序

### 使用场景

根据决策 D-04，虚拟线程适用于：

**✅ 适用场景：**
- HTTP 请求处理（REST API、WebSocket）
- 数据库查询和事务处理
- 文件 I/O 操作
- 外部 API 调用
- 消息队列处理

**❌ 不适用场景：**
- CPU 密集型计算任务
- 长时间运行的同步计算
- 需要精确线程控制的场景

### 代码示例

```java
// 示例 1：使用虚拟线程执行器处理 HTTP 请求
public class VirtualThreadExample {
    
    public CompletableFuture<List<String>> fetchMultipleUrls(List<String> urls) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<String>> futures = urls.stream()
                .map(url -> CompletableFuture.supplyAsync(() -> fetchUrl(url), executor))
                .toList();
            
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                    .map(CompletableFuture::join)
                    .toList());
        }
    }
    
    private String fetchUrl(String url) {
        try {
            // 模拟 HTTP 请求
            return HttpClient.newHttpClient()
                .send(HttpRequest.newBuilder(URI.create(url)).build(),
                      HttpResponse.BodyHandlers.ofString())
                .body();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch URL: " + url, e);
        }
    }
}

// 示例 2：Spring Boot 中的虚拟线程配置
@Configuration
public class VirtualThreadConfig {
    
    @Bean
    public TaskExecutor virtualThreadTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }
    
    @Bean
    public AsyncTaskExecutor asyncTaskExecutor() {
        return new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor());
    }
}
```

### 注意事项

1. **避免在虚拟线程中使用 ThreadLocal**
   - 虚拟线程频繁挂起/恢复，ThreadLocal 可能导致内存泄漏
   - 使用 Scoped Values 替代 ThreadLocal（见 Scoped Values 章节）

2. **正确关闭资源**
   - 使用 try-with-resources 确保 ExecutorService 正确关闭
   - 避免在 finally 块中执行阻塞操作

3. **监控和调试**
   - 虚拟线程的堆栈跟踪可能较长，需要适当配置日志级别
   - 使用 JFR（Java Flight Recorder）监控虚拟线程性能

4. **与现有代码兼容**
   - 虚拟线程兼容现有 java.util.concurrent API
   - 无需修改现有使用 ExecutorService 的代码

## Pattern Matching

### 概念说明

模式匹配（Pattern Matching）是 JDK 21 引入的增强特性，在 JDK 25 中进一步优化。它允许在类型检查和类型转换的同时进行数据提取，简化了复杂的条件逻辑。

**主要特性：**
- **instanceof 模式匹配**：在类型检查的同时绑定变量
- **switch 表达式模式匹配**：在 switch 中根据类型和值进行模式匹配
- **记录类模式**：解构记录类（Record）的字段

### 使用场景

根据决策 D-05，模式匹配优先用于：

**✅ 优先使用场景：**
- 记录类（Record）的解构和字段提取
- switch 表达式中的类型匹配
- 复杂条件逻辑的简化
- 数据验证和转换

### 代码示例

```java
// 示例 1：记录类模式匹配
public record User(String name, int age, String email) {}
public record Order(String id, BigDecimal amount, User user) {}

public class PatternMatchingExample {
    
    public String processUser(Object obj) {
        // 使用 switch 表达式进行模式匹配
        return switch (obj) {
            case User(String name, int age, String email) when age >= 18 -> 
                "成人用户: " + name + " (" + email + ")";
            case User(String name, int age, String email) -> 
                "未成年用户: " + name;
            case Order(String id, BigDecimal amount, User user) when amount.compareTo(BigDecimal.valueOf(1000)) > 0 ->
                "大额订单: " + id + " - 用户: " + user.name();
            case Order(String id, BigDecimal amount, User user) ->
                "普通订单: " + id;
            case null -> 
                "空对象";
            default -> 
                "未知类型: " + obj.getClass().getSimpleName();
        };
    }
    
    // 示例 2：instanceof 模式匹配
    public void validateAndProcess(Object data) {
        if (data instanceof User(String name, int age, String email)) {
            // 直接使用解构出的变量
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("邮箱不能为空");
            }
            System.out.println("处理用户: " + name);
        } else if (data instanceof String str && !str.isBlank()) {
            System.out.println("处理字符串: " + str);
        }
    }
}

// 示例 3：Spring Boot 中的模式匹配应用
@Service
public class NotificationService {
    
    public void sendNotification(Object event) {
        switch (event) {
            case UserRegisteredEvent(String username, String email, LocalDateTime registeredAt) ->
                sendWelcomeEmail(username, email);
            case OrderCreatedEvent(String orderId, BigDecimal amount, String customerEmail) ->
                sendOrderConfirmation(orderId, amount, customerEmail);
            case PaymentCompletedEvent(String paymentId, BigDecimal amount, boolean success) when success ->
                sendPaymentSuccessNotification(paymentId, amount);
            default ->
                log.warn("未知事件类型: {}", event.getClass().getName());
        }
    }
    
    private void sendWelcomeEmail(String username, String email) {
        // 发送欢迎邮件逻辑
    }
    
    private void sendOrderConfirmation(String orderId, BigDecimal amount, String email) {
        // 发送订单确认逻辑
    }
    
    private void sendPaymentSuccessNotification(String paymentId, BigDecimal amount) {
        // 发送支付成功通知逻辑
    }
}
```

### 注意事项

1. **类型安全**
   - 模式匹配在编译时进行类型检查
   - 确保所有可能的模式都被覆盖，或提供 default 分支

2. **可读性**
   - 避免过度复杂的嵌套模式
   - 使用 when 子句进行条件过滤，保持代码清晰

3. **性能考虑**
   - 模式匹配的性能与传统的 instanceof 和 switch 相当
   - 复杂的模式可能影响性能，在性能关键路径上谨慎使用

4. **与现有代码兼容**
   - 模式匹配是向后兼容的语法增强
   - 可以逐步替换现有的 instanceof 和 switch 语句

## Scoped Values

### 概念说明

作用域值（Scoped Values）是 JDK 21 引入的正式特性，用于替代 ThreadLocal 和 InheritableThreadLocal。它提供了一种更安全、更高效的方式在调用链中传递上下文数据。

**主要优势：**
- **不可变性**：一旦绑定就不能修改
- **结构化生命周期**：明确的作用域边界
- **内存安全**：避免内存泄漏问题
- **虚拟线程友好**：专为虚拟线程设计

### 使用场景

根据决策 D-06，Scoped Values 用于替代：

**✅ 替代场景：**
- ThreadLocal 存储请求上下文（如用户信息、跟踪ID）
- InheritableThreadLocal 传递上下文给子线程
- 跨方法调用的隐式参数传递
- 事务上下文管理

### 代码示例

```java
// 示例 1：定义和使用 Scoped Values
public class RequestContext {
    
    // 定义 Scoped Value
    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();
    private static final ScopedValue<User> CURRENT_USER = ScopedValue.newInstance();
    private static final ScopedValue<Locale> USER_LOCALE = ScopedValue.newInstance();
    
    public void handleRequest(HttpServletRequest request) {
        // 绑定多个 Scoped Values
        ScopedValue.where(REQUEST_ID, generateRequestId())
                   .where(CURRENT_USER, extractUser(request))
                   .where(USER_LOCALE, determineLocale(request))
                   .run(() -> processRequest(request));
    }
    
    private void processRequest(HttpServletRequest request) {
        // 在作用域内访问 Scoped Values
        String requestId = REQUEST_ID.get();
        User user = CURRENT_USER.get();
        Locale locale = USER_LOCALE.get();
        
        log.info("处理请求 {}，用户: {}, 区域: {}", requestId, user.username(), locale);
        
        // 调用其他方法，上下文自动传递
        validateRequest();
        processBusinessLogic();
    }
    
    private void validateRequest() {
        // 可以直接访问 Scoped Values
        User user = CURRENT_USER.get();
        if (!user.hasPermission("request.validate")) {
            throw new SecurityException("用户无权限");
        }
    }
}

// 示例 2：Spring Boot 中的 Scoped Values 集成
@Component
public class ScopedValuesInterceptor implements HandlerInterceptor {
    
    private static final ScopedValue<String> TRACE_ID = ScopedValue.newInstance();
    private static final ScopedValue<Authentication> AUTH = ScopedValue.newInstance();
    
    @Override
    public boolean preHandle(HttpServletRequest request, 
                            HttpServletResponse response, 
                            Object handler) {
        // 在请求开始时绑定 Scoped Values
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId == null) {
            traceId = UUID.randomUUID().toString();
        }
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        ScopedValue.where(TRACE_ID, traceId)
                   .where(AUTH, auth)
                   .run(() -> {
                       // 请求处理逻辑
                   });
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, 
                               HttpServletResponse response, 
                               Object handler, 
                               Exception ex) {
        // 请求结束后自动清理 Scoped Values
    }
}

// 示例 3：异步任务中的 Scoped Values
@Service
public class AsyncService {
    
    private static final ScopedValue<String> REQUEST_ID = ScopedValue.newInstance();
    
    @Async
    public CompletableFuture<String> asyncProcess(String data) {
        // 在异步任务中访问 Scoped Values
        String requestId = REQUEST_ID.get();
        log.info("异步处理请求 {}，数据: {}", requestId, data);
        
        return CompletableFuture.completedFuture("processed: " + data);
    }
}
```

### 注意事项

1. **生命周期管理**
   - Scoped Values 的作用域由 where().run() 界定
   - 确保在适当的作用域内绑定和访问

2. **不可变性**
   - 一旦绑定就不能修改
   - 如果需要"修改"值，需要创建新的作用域

3. **性能考虑**
   - Scoped Values 比 ThreadLocal 更高效
   - 避免在紧密循环中频繁创建作用域

4. **错误处理**
   - 在未绑定的作用域中调用 get() 会抛出异常
   - 使用 orElse() 提供默认值

## Value Types

### 概念说明

值类型（Value Types）是 JDK 25 中的前瞻性特性（当前为预览特性）。根据决策 D-07，本规范仅作为前瞻性说明，等待正式发布后引入。

**核心概念：**
- **值语义**：按值传递，而不是按引用传递
- **不可变性**：值类型实例是不可变的
- **内存效率**：可能存储在栈上，减少堆内存分配
- **性能优化**：减少对象头开销，提高缓存局部性

### 使用场景

**⚠️ 当前状态：**
- JDK 25 中可能为预览特性
- 等待正式发布并稳定后引入
- 当前仅作为技术前瞻性了解

### 代码示例（基于当前提案）

```java
// 示例：值类型的简单定义（语法可能变化）
// 注意：这是基于当前提案的示例，实际语法可能不同

// 值类型定义
value class Point {
    private final double x;
    private final double y;
    
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public double x() { return x; }
    public double y() { return y; }
    
    public Point translate(double dx, double dy) {
        return new Point(x + dx, y + dy);
    }
}

// 使用示例
public class ValueTypeExample {
    
    public void processPoints() {
        Point p1 = new Point(1.0, 2.0);
        Point p2 = new Point(3.0, 4.0);
        
        // 值语义：按值传递
        Point p3 = p1.translate(5.0, 6.0);
        
        // 比较基于值，而不是引用
        boolean equal = p1.equals(new Point(1.0, 2.0)); // true
    }
}
```

### 注意事项

1. **预览特性警告**
   - 当前为预览特性，API 可能变化
   - 不推荐在生产环境中使用

2. **迁移准备**
   - 了解值类型的概念和优势
   - 识别可能受益于值类型的现有代码
   - 等待正式发布后制定详细的迁移指南

3. **性能影响**
   - 值类型可能显著减少内存分配
   - 对性能敏感的数据结构可能受益最大

4. **兼容性考虑**
   - 值类型与现有引用类型的互操作
   - 序列化/反序列化支持
   - 框架集成（如 Spring、Jackson）

## 最佳实践

### 1. 特性选择指南

| 特性 | 适用场景 | 不适用场景 | 优先级 |
|------|----------|------------|--------|
| Virtual Threads | I/O 密集型任务 | CPU 密集型任务 | 高 |
| Pattern Matching | 记录类解构、switch 表达式 | 简单类型检查 | 中 |
| Scoped Values | 请求上下文传递 | 频繁修改的共享状态 | 高 |
| Value Types | 性能敏感的数据结构 | 稳定的生产代码（等待正式发布） | 低 |

### 2. 代码审查清单

**Virtual Threads:**
- [ ] 是否用于 I/O 密集型任务？
- [ ] 是否避免了 CPU 密集型任务？
- [ ] 是否正确关闭了 ExecutorService？
- [ ] 是否避免了 ThreadLocal 的使用？

**Pattern Matching:**
- [ ] 是否提高了代码可读性？
- [ ] 是否覆盖了所有可能的模式？
- [ ] 是否避免了过度复杂的嵌套？
- [ ] 是否提供了有意义的默认分支？

**Scoped Values:**
- [ ] 是否替代了 ThreadLocal？
- [ ] 作用域边界是否清晰？
- [ ] 是否处理了未绑定值的异常情况？
- [ ] 是否考虑了异步上下文传递？

### 3. 性能优化建议

1. **Virtual Threads**
   - 监控虚拟线程池的使用情况
   - 避免在虚拟线程中执行阻塞的同步操作
   - 使用适当的线程池大小配置

2. **Pattern Matching**
   - 在性能关键路径上测试模式匹配的性能
   - 优先使用简单的模式，避免深度嵌套
   - 考虑使用 when 子句提前过滤

3. **Scoped Values**
   - 避免在紧密循环中频繁创建作用域
   - 合理设计上下文数据结构，减少绑定次数
   - 监控作用域创建和销毁的性能

### 4. 测试策略

1. **单元测试**
   - 测试每个特性的基本功能
   - 验证边界条件和异常情况
   - 模拟不同的使用场景

2. **集成测试**
   - 测试特性在 Spring Boot 环境中的集成
   - 验证上下文传递的正确性
   - 测试异步场景下的行为

3. **性能测试**
   - 对比新旧实现的性能差异
   - 测试高并发场景下的稳定性
   - 监控内存使用和垃圾回收

## 参考资源

### 官方文档
- [JDK 25 Release Notes](https://openjdk.org/jeps/) - JDK 25 特性列表
- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444) - 虚拟线程规范
- [JEP 441: Pattern Matching for switch](https://openjdk.org/jeps/441) - switch 模式匹配
- [JEP 446: Scoped Values](https://openjdk.org/jeps/446) - 作用域值
- [JEP 401: Value Objects](https://openjdk.org/jeps/401) - 值对象（预览）

### 学习资源
- [Java Concurrency in Practice](https://jcip.net/) - Java 并发编程实践
- [Modern Java in Action](https://www.manning.com/books/modern-java-in-action) - 现代 Java 特性详解
- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/reference/) - Spring Boot 官方文档

### 工具支持
- **IDE**: IntelliJ IDEA 2024.1+、Eclipse 2024-03+ 支持 JDK 25 特性
- **构建工具**: Maven 3.9.x、Gradle 8.5+ 支持 JDK 25 编译
- **监控工具**: JFR、Micrometer、Prometheus 支持虚拟线程监控

### 社区资源
- [OpenJDK Mailing Lists](https://mail.openjdk.org/) - OpenJDK 邮件列表
- [Spring Community](https://spring.io/community) - Spring 社区
- [Stack Overflow](https://stackoverflow.com/questions/tagged/java) - Java 相关问题

---

**文档版本:** 1.0  
**创建日期:** 2026-05-29  
**最后更新:** 2026-05-29  
**维护者:** Hadoken Framework 开发团队  

**变更记录:**
- 1.0 (2026-05-29): 初始版本，基于 JDK 25 正式特性和用户决策 D-01 到 D-10