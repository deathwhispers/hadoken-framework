# Feature Landscape

**Domain:** JDK 25 + Spring Boot Upgrade Features
**Researched:** 2026-05-29

## Table Stakes

Features expected in a 2026 enterprise Spring Boot framework. Missing = product feels incomplete.

| Feature | Why Expected | Complexity | Notes |
|---------|--------------|------------|-------|
| JDK 25 Support | Modern LTS version | Low | 编译配置变更 |
| Virtual Threads | Performance expectation | Medium | Spring Boot 3.2+ 内置支持 |
| Pattern Matching | Code quality expectation | Low | JDK 21+ 特性，JDK 25 增强 |
| Records Support | Modern Java data classes | Low | JDK 14+ 引入，JDK 25 增强 |
| Sealed Classes | Type safety expectation | Low | JDK 17+ 正式，JDK 25 增强模式匹配 |
| Spring Boot 3.5+ | Latest security patches | Low | 版本升级 |
| Spring Security 6.x | Modern security | Medium | Spring Boot 3.x 内置 |

## Differentiators

Features that set product apart after upgrade.

| Feature | Value Proposition | Complexity | Notes |
|---------|-------------------|------------|-------|
| JDK 25 LTS | Long-term support to 2032 | Low | 企业级稳定性保证 |
| Value Types (JEP 401) | Memory efficiency, performance | High | JDK 25 预览/正式特性 |
| Structured Concurrency | Simplified async programming | Medium | JDK 21+ 预览，JDK 25 增强 |
| Scoped Values | Better than ThreadLocal | Medium | 替代 InheritableThreadLocal |
| String Templates | SQL/JSON safety | Low | 防止注入攻击 |
| Foreign Function & Memory API | Native interop | High | 替代 JNI |

## Anti-Features

Features to explicitly NOT build during upgrade.

| Anti-Feature | Why Avoid | What to Do Instead |
|--------------|-----------|-------------------|
| JDK 17 Compatibility | 用户明确要求激进升级 | 只支持 JDK 25 |
| Legacy API Support | 增加维护负担 | 使用现代 API |
| Polyglot Support | 超出框架范围 | 保持纯 Java |
| AOT Compilation | 增加复杂度 | 保持 JIT 模式 |

## Feature Dependencies

```
JDK 25
├── Virtual Threads (JDK 21+)
│   └── Structured Concurrency (JDK 21+ 预览)
├── Pattern Matching
│   ├── Record Patterns (JDK 21)
│   └── Switch Pattern Matching (JDK 21)
├── Value Types (JEP 401, 预览/正式)
└── Foreign Function & Memory API (JDK 22+)

Spring Boot 3.5.x
├── Spring Framework 6.2.x
├── Spring Cloud 2025.0.x
│   └── Spring Cloud Alibaba 2023.0.3.x
└── Spring Security 6.x
```

## Upgrade Feature Matrix

### JDK 25 New Features (需验证)

| Feature | JEP | Status | Impact on Framework |
|---------|-----|--------|---------------------|
| Value Types | 401 | Preview/正式 | 高 - API 设计影响 |
| Structured Concurrency | 453 | 正式 | 高 - 异步代码简化 |
| Scoped Values | 446 | 正式 | 中 - ThreadLocal 替代 |
| String Templates | 430 | 正式 | 中 - SQL/JSON 安全 |
| Implicitly Declared Classes | 463 | Preview | 低 - 代码简化 |
| Pattern Matching for switch | 441 | 正式 | 低 - 代码简化 |

### Spring Boot 3.5 Features (需验证)

| Feature | Impact on Framework |
|---------|---------------------|
| Virtual Threads 默认启用 | 异步代码优化 |
| Observability 增强 | 监控集成优化 |
| Native Image 支持 | 可选原生编译 |
| HTTP/3 支持 | 网络层优化 |

## MVP Recommendation

Prioritize:
1. JDK 25 升级 + 构建验证
2. Spring Boot 3.5.x 升级 + 兼容性验证
3. Virtual Threads 启用验证

Defer:
- Value Types 采用: 等待正式发布并稳定后
- String Templates: 可渐进式引入
- Native Image: 超出当前范围

## Sources

- OpenJDK JEP Index (需验证): https://openjdk.org/jeps/
- Spring Boot Release Notes (需验证): https://github.com/spring-projects/spring-boot/releases
- Spring Framework 6.x Features: https://docs.spring.io/spring-framework/reference/

---

*Feature analysis: 2026-05-29*