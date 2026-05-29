# Technology Stack Research

**Project:** Hadoken Framework
**Research Focus:** JDK 25 + Spring Boot Upgrade Stack
**Researched:** 2026-05-29
**Confidence:** MEDIUM (网络访问受限，部分版本信息需要验证)

## Executive Summary

本研究分析从 JDK 17 + Spring Boot 3.3.13 升级到 JDK 25 + 兼容 Spring Boot 版本的技术栈。

**关键发现:**
- JDK 25 是 2025 年 9 月发布的 LTS 版本 (需验证)
- Spring Boot 3.5.x 是支持 JDK 25 的推荐版本 (需验证)
- 大部分依赖已有 JDK 25 兼容版本

---

## Current Stack (现状)

| 组件 | 当前版本 | 来源 |
|------|----------|------|
| JDK | 17 (运行 21) | pom.xml |
| Spring Boot | 3.3.13 | hadoken-dependencies/pom.xml |
| Spring Cloud | 2025.0.0 | hadoken-dependencies/pom.xml |
| Spring Cloud Alibaba | 2023.0.1.0 | hadoken-dependencies/pom.xml |

---

## Recommended Stack (推荐升级)

### Core Framework

| 组件 | 推荐版本 | 置信度 | 说明 |
|------|----------|--------|------|
| **JDK** | 25 (LTS) | MEDIUM | 2025年9月发布，LTS版本，支持到2032年 (需验证) |
| **Spring Boot** | 3.5.x | MEDIUM | 支持 JDK 25 的稳定版本 (需验证官方兼容矩阵) |
| **Spring Cloud** | 2025.0.x | MEDIUM | 与 Spring Boot 3.5.x 兼容的版本 |
| **Spring Cloud Alibaba** | 2023.0.3.x | MEDIUM | 需要 2023.0.3.0+ 以支持 Spring Boot 3.5.x (需验证) |

### Database & ORM

| 组件 | 当前版本 | 推荐版本 | 置信度 | 说明 |
|------|----------|----------|--------|------|
| **MyBatis-Plus** | 3.5.12 | 3.5.12+ | HIGH | 已支持 JDK 21+，JDK 25 兼容性好 |
| **Druid** | 1.2.24 | 1.2.24+ | HIGH | Spring Boot 3 兼容 |
| **MySQL Connector** | 8.4.0 | 9.x | MEDIUM | MySQL 9.x 更好支持 JDK 25 (需验证) |
| **Dynamic Datasource** | 4.3.1 | 4.3.1+ | HIGH | 基于 MyBatis-Plus，兼容性良好 |

### Redis & Cache

| 组件 | 当前版本 | 推荐版本 | 置信度 | 说明 |
|------|----------|----------|--------|------|
| **Redisson** | 3.45.1 | 3.50+ | HIGH | Redisson 持续更新，JDK 25 兼容性好 |
| **Spring Data Redis** | (Spring Boot 管理) | 随 Spring Boot | HIGH | 由 Spring Boot BOM 管理 |

### Tools & Utilities

| 组件 | 当前版本 | 推荐版本 | 置信度 | 说明 |
|------|----------|----------|--------|------|
| **Hutool** | 5.8.39 | 6.x | MEDIUM | Hutool 6.x 专为 JDK 21+ 设计 (需验证稳定性) |
| **Lombok** | 1.18.38 | 1.18.38+ | HIGH | 持续更新，JDK 25 兼容 |
| **MapStruct** | 1.6.3 | 1.6.3+ | HIGH | 编译时代码生成，JDK 版本无关 |
| **Guava** | 33.4.8-jre | 34.x+ | HIGH | Google 持续更新 |
| **Fastjson2** | 2.0.57 | 2.0.x (latest) | HIGH | 持续更新，JDK 兼容性好 |

### API Documentation

| 组件 | 当前版本 | 推荐版本 | 置信度 | 说明 |
|------|----------|----------|--------|------|
| **SpringDoc** | 2.6.0 | 2.8.x+ | MEDIUM | 需匹配 Spring Boot 3.5.x (需验证) |
| **Knife4j** | 4.5.0 | 5.x | MEDIUM | Knife4j 5.x 支持 Spring Boot 3.5.x (需验证) |

### Monitoring & Observability

| 组件 | 当前版本 | 推荐版本 | 置信度 | 说明 |
|------|----------|----------|--------|------|
| **SkyWalking** | 9.0.0 | 10.x | MEDIUM | 新版本支持更好 (需验证) |
| **Spring Boot Admin** | 3.3.1 | 3.5.x | HIGH | 版本与 Spring Boot 保持一致 |

### Build Tools

| 组件 | 当前版本 | 推荐版本 | 置信度 | 说明 |
|------|----------|----------|--------|------|
| **Maven** | 3.6.3 | 3.9.x | HIGH | Maven 3.9.x 更好支持 JDK 25 |
| **Maven Compiler Plugin** | 3.12.1 | 3.14.x | HIGH | 支持 JDK 25 的 compiler plugin |

---

## Compatibility Matrix (兼容性矩阵)

### JDK 25 Feature Support

**JDK 25 新特性 (需验证):**

| JEP | 特性 | 相关性 |
|-----|------|--------|
| Value Types (预览/正式) | 值类型 | 高 - 可能影响 API 设计 |
| Pattern Matching | 记录类模式匹配增强 | 高 - 代码简化 |
| Virtual Threads | 虚拟线程增强 | 高 - 并发优化 |
| String Templates | 字符串模板 (正式) | 中 - 代码可读性 |
| Scoped Values | 作用域值 | 中 - 替代 ThreadLocal |
| Structured Concurrency | 结构化并发 | 高 - 并发模型 |

### Spring Boot Version Compatibility

```
Spring Boot 3.5.x
├── JDK 17-25 (LTS 线)
├── Spring Framework 6.2.x
├── Spring Cloud 2025.0.x
├── Spring Cloud Alibaba 2023.0.3.x
└── Jakarta EE 10
```

---

## Migration Path (迁移路径)

### Phase 1: JDK 升级

```xml
<!-- pom.xml -->
<properties>
    <java.version>25</java.version>
    <maven-compiler-plugin.version>3.14.0</maven-compiler-plugin.version>
</properties>
```

**注意事项:**
1. 确保 Maven 3.9.x+
2. 更新 IDE 到支持 JDK 25 的版本
3. CI/CD 环境需要 JDK 25

### Phase 2: Spring Boot 升级

```xml
<!-- hadoken-dependencies/pom.xml -->
<properties>
    <spring-boot.version>3.5.x</spring-boot.version>
    <spring-cloud.version>2025.0.x</spring-cloud.version>
    <spring-cloud-alibaba.version>2023.0.3.x</spring-cloud-alibaba.version>
</properties>
```

**破坏性变更预期 (需验证):**
1. Spring Boot 3.4+ 可能移除部分废弃 API
2. Spring Cloud Alibaba 版本必须匹配
3. SpringDoc 版本需要升级

### Phase 3: 依赖升级

**优先级排序:**

1. **高优先级 - 核心依赖**
   - Hutool 5.x → 6.x (重大变更，需要测试)
   - SpringDoc 2.6.0 → 2.8.x+
   - Knife4j 4.5.0 → 5.x

2. **中优先级 - 数据层**
   - MySQL Connector 8.4.0 → 9.x
   - Redisson 保持更新

3. **低优先级 - 工具类**
   - Guava、Fastjson2 等保持最新

---

## Alternatives Considered (替代方案)

| 组件 | 当前选择 | 替代方案 | 不选择原因 |
|------|----------|----------|------------|
| Hutool 6.x | Hutool 5.8.39 | Hutool 5.x | 6.x 专为 JDK 21+ 设计，但稳定性需验证 |
| Spring Cloud Alibaba | 2023.0.3.x | Spring Cloud Tencent | 已有生态，迁移成本高 |
| Maven | Maven 3.9.x | Gradle 8.x | 项目已使用 Maven，迁移成本高 |

---

## Risk Assessment (风险评估)

### 高风险

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| Spring Cloud Alibaba 版本不兼容 | 微服务功能失效 | 等待官方发布兼容版本 |
| Hutool 6.x 破坏性变更 | 大量代码修改 | 保持 5.x 或增量迁移 |

### 中风险

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| SpringDoc 版本不匹配 | API 文档功能失效 | 升级到兼容版本 |
| 第三方依赖不支持 JDK 25 | 编译/运行错误 | 查找替代方案 |

### 低风险

| 风险 | 影响 | 缓解措施 |
|------|------|----------|
| Maven 插件版本 | 构建警告 | 更新插件版本 |

---

## Version Verification Commands (版本验证命令)

```bash
# 验证 JDK 版本
java -version

# 验证 Maven 版本
mvn -version

# 验证依赖树
mvn dependency:tree

# 编译测试
mvn clean compile -DskipTests

# 运行测试
mvn test
```

---

## Recommended Upgrade Order (推荐升级顺序)

1. **Maven** → 3.9.x (构建工具)
2. **JDK** → 25 (语言版本)
3. **Spring Boot** → 3.5.x (核心框架)
4. **Spring Cloud** → 2025.0.x (微服务)
5. **Spring Cloud Alibaba** → 2023.0.3.x (阿里云组件)
6. **MyBatis-Plus** → 保持/升级
7. **Hutool** → 6.x (可选，需评估)
8. **其他依赖** → 最新兼容版本

---

## Verification Required (需要验证的信息)

以下信息由于网络访问限制，**需要在执行前验证:**

### 必须验证

- [ ] JDK 25 正式发布日期和 LTS 支持周期
- [ ] Spring Boot 3.5.x 是否已发布并支持 JDK 25
- [ ] Spring Cloud 2025.0.x 与 Spring Boot 3.5.x 的兼容性
- [ ] Spring Cloud Alibaba 2023.0.3.x 与 Spring Boot 3.5.x 的兼容性

### 建议验证

- [ ] Hutool 6.x 的稳定性和 JDK 25 兼容性
- [ ] SpringDoc 2.8.x 与 Spring Boot 3.5.x 兼容性
- [ ] Knife4j 5.x 发布状态
- [ ] MySQL Connector 9.x 兼容性
- [ ] Redisson 3.50+ 与 JDK 25 兼容性

---

## Sources

**置信度说明:**
- HIGH: 官方文档确认或历史版本模式推断
- MEDIUM: 基于训练数据推断，需验证
- LOW: 推测，必须验证

**参考来源 (需访问验证):**
- OpenJDK: https://openjdk.org/projects/jdk/25/
- Spring Boot: https://spring.io/projects/spring-boot
- Spring Cloud: https://spring.io/projects/spring-cloud
- Spring Cloud Alibaba: https://github.com/alibaba/spring-cloud-alibaba
- MyBatis-Plus: https://github.com/baomidou/mybatis-plus
- Redisson: https://github.com/redisson/redisson
- Hutool: https://github.com/dromara/hutool

---

*Research completed: 2026-05-29*
*Note: 网络访问受限，部分版本信息需要手动验证*