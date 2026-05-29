# Phase 3: Core Framework Upgrade - Research

**Researched:** 2026-05-29
**Domain:** Spring Boot、Spring Cloud、Spring Cloud Alibaba 版本升级
**Confidence:** MEDIUM

## Summary

本阶段研究聚焦于将 Hadoken Framework 的核心框架从当前版本（Spring Boot 3.3.13、Spring Cloud 2025.0.0、Spring Cloud Alibaba 2023.0.1.0）升级到支持 JDK 25 的兼容版本。根据用户决策，采用激进升级策略，使用最新稳定版本。

**核心挑战：**
1. Spring Boot 3.5.x 的发布状态和 JDK 25 支持程度需要验证 [ASSUMED]
2. Spring Cloud 与 Spring Boot 3.5.x 的兼容版本矩阵需要确认 [ASSUMED]
3. Spring Cloud Alibaba 与 Spring Cloud 2025.x 的兼容性需要验证 [ASSUMED]

**主要发现：**
- Spring Boot 3.5.x 是官方支持的 JDK 25 版本 [ASSUMED]
- Spring Cloud 2025.0.x 与 Spring Boot 3.5.x 兼容 [ASSUMED]
- Spring Cloud Alibaba 2023.0.1.0+ 可能与 Spring Cloud 2025.x 存在兼容性问题 [ASSUMED]

**主要建议：** 通过 hadoken-dependencies BOM 集中管理版本升级，采用分步验证策略确保兼容性。

## Architectural Responsibility Map

| Capability | Primary Tier | Secondary Tier | Rationale |
|------------|-------------|----------------|-----------|
| Spring Boot核心框架升级 | 构建/运行时 | 无 | Spring Boot版本直接影响构建配置和运行时环境 |
| Spring Cloud版本兼容 | 构建/运行时 | 无 | Spring Cloud依赖Spring Boot版本，必须在构建时确保兼容 |
| Spring Cloud Alibaba兼容 | 构建/运行时 | 无 | Alibaba组件依赖Spring Cloud和Spring Boot版本链 |
| Spring Framework版本管理 | 构建 | 无 | 通过Spring Boot BOM自动管理，无需手动配置 |
| Jakarta EE版本管理 | 构建 | 无 | 通过Spring Boot BOM自动管理，确保版本一致性 |

## Standard Stack

### Core
| Library | Version | Purpose | Why Standard |
|---------|---------|---------|--------------|
| Spring Boot | 3.5.0+ | 企业级Java应用框架 | JDK 25官方支持的最新稳定版本 [ASSUMED] |
| Spring Cloud | 2025.0.x | 微服务框架 | 与Spring Boot 3.5.x兼容的发布列车 [ASSUMED] |
| Spring Cloud Alibaba | 2023.0.1.0+ | 阿里云微服务组件 | 与Spring Cloud 2025.x兼容的最新版本 [ASSUMED] |

### Supporting
| Library | Version | Purpose | When to Use |
|---------|---------|---------|-------------|
| Spring Framework | 6.2.x | Spring核心框架 | 通过Spring Boot BOM自动管理 |
| Jakarta EE | 10.x | Jakarta企业版API | 通过Spring Boot BOM自动管理 |

### Alternatives Considered
| Instead of | Could Use | Tradeoff |
|------------|-----------|----------|
| Spring Boot 3.5.x | Spring Boot 4.x | 4.x未发布，3.5.x是当前支持JDK 25的最新稳定版 |
| Spring Cloud 2025.x | Spring Cloud 2023.x | 2025.x与Spring Boot 3.5.x兼容性更好 |
| Spring Cloud Alibaba | 移除依赖 | 如无兼容版本，可考虑移除或降级Spring Cloud版本 |

**安装：**
```bash
# 版本升级通过修改pom.xml属性完成，无需额外安装
```

**版本验证：** 
- Spring Boot版本需要验证官方发布状态和JDK 25支持 [需要验证]
- Spring Cloud兼容性矩阵需要确认 [需要验证]
- Spring Cloud Alibaba兼容性需要特别验证 [需要验证]

## Architecture Patterns

### System Architecture Diagram

```
[业务应用]
    │
    ├─[Spring Boot 3.5.x应用层]
    │   ├─[Web MVC] ──[REST API]──[用户请求]
    │   ├─[Security] ──[认证/授权]──[安全上下文]
    │   └─[Auto-configuration] ──[自动装配]──[Starter模块]
    │
    ├─[Spring Cloud 2025.x微服务层]
    │   ├─[服务发现] ──[Eureka/Nacos]──[服务注册]
    │   ├─[配置中心] ──[Config Server]──[配置管理]
    │   └─[网关路由] ──[Gateway]──[请求转发]
    │
    └─[Spring Cloud Alibaba云服务层]
        ├─[Nacos] ──[服务注册与发现]
        ├─[Sentinel] ──[流量控制]
        └─[Seata] ──[分布式事务]
```

### Recommended Project Structure
```
hadoken-framework/
├── hadoken-dependencies/          # BOM版本管理
│   └── pom.xml                   # 核心版本属性定义
├── hadoken-common/               # 公共工具类
├── hadoken-web-spring-boot-starter/      # Web增强
├── hadoken-security-spring-boot-starter/ # 安全增强
├── ... (其他Starter模块)
└── pom.xml                       # 根POM，继承Spring Boot Parent
```

### Pattern 1: BOM集中版本管理
**What:** 通过hadoken-dependencies模块统一管理所有依赖版本
**When to use:** 多模块项目中需要统一版本管理
**Example:**
```xml
<!-- hadoken-dependencies/pom.xml -->
<properties>
    <spring-boot.version>3.5.0</spring-boot.version>
    <spring-cloud.version>2025.0.0</spring-cloud.version>
    <spring-cloud-alibaba.version>2023.0.1.0</spring-cloud-alibaba.version>
</properties>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${spring-boot.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Pattern 2: Spring Boot Parent继承
**What:** 根POM继承spring-boot-starter-parent以获得默认配置
**When to use:** Spring Boot项目标准配置模式
**Example:**
```xml
<!-- pom.xml -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.0</version>
</parent>
```

### Anti-Patterns to Avoid
- **分散版本管理:** 避免在各模块中单独指定版本，应通过BOM集中管理
- **硬编码版本号:** 避免在dependency中硬编码版本，应使用属性引用
- **忽略兼容性矩阵:** 升级前必须验证Spring官方兼容性矩阵

## Don't Hand-Roll

| Problem | Don't Build | Use Instead | Why |
|---------|-------------|-------------|-----|
| 版本兼容性检查 | 手动检查每个依赖的兼容性 | Spring Boot BOM + Spring官方兼容性矩阵 | BOM自动管理传递依赖，确保版本一致性 |
| 破坏性变更处理 | 手动查找和修复每个破坏性变更 | Spring Boot升级指南 + 官方迁移文档 | 官方文档提供完整的破坏性变更列表和迁移指南 |
| 微服务组件集成 | 手动集成服务发现、配置中心等 | Spring Cloud标准组件 | Spring Cloud提供标准化的微服务解决方案 |

**关键洞察：** Spring生态系统的版本管理高度复杂，手动管理容易导致版本冲突和兼容性问题。使用Spring Boot BOM和官方兼容性矩阵是避免这些问题的最佳实践。

## Common Pitfalls

### Pitfall 1: 版本兼容性链断裂
**What goes wrong:** Spring Boot、Spring Cloud、Spring Cloud Alibaba版本不兼容导致启动失败
**Why it happens:** 三个框架的版本需要形成完整的兼容链
**How to avoid:** 严格遵循Spring官方兼容性矩阵，按顺序验证兼容性
**Warning signs:** ClassNotFoundException、NoSuchMethodError、Bean创建失败

### Pitfall 2: Jakarta EE命名空间冲突
**What goes wrong:** javax.* 和 jakarta.* 包冲突
**Why it happens:** Spring Boot 3.x使用Jakarta EE 9+，但某些依赖仍使用javax.*
**How to avoid:** 确保所有依赖都支持Jakarta EE 9+
**Warning signs:** 编译错误、类加载失败

### Pitfall 3: 自动配置冲突
**What goes wrong:** 多个Starter的自动配置相互冲突
**Why it happens:** @Conditional配置条件重叠或冲突
**How to avoid:** 使用@ConditionalOnMissingBean、明确排除不需要的自动配置
**Warning signs:** Bean重复定义、配置属性不生效

### Pitfall 4: 测试框架兼容性
**What goes wrong:** 单元测试在版本升级后失败
**Why it happens:** Spring Boot Test、Mockito等测试框架版本不兼容
**How to avoid:** 同步升级测试相关依赖，使用Spring Boot Test的兼容版本
**Warning signs:** 测试运行失败、Mockito异常、上下文加载失败

## Code Examples

### Spring Boot版本升级示例
```xml
<!-- hadoken-dependencies/pom.xml -->
<properties>
    <!-- 从3.3.13升级到3.5.0 -->
    <spring-boot.version>3.5.0</spring-boot.version>
    <!-- 保持2025.0.0或升级到兼容版本 -->
    <spring-cloud.version>2025.0.0</spring-cloud.version>
    <!-- 验证与Spring Cloud 2025.x的兼容性 -->
    <spring-cloud-alibaba.version>2023.0.1.0</spring-cloud-alibaba.version>
</properties>
```

### 兼容性验证测试示例
```java
@SpringBootTest
class FrameworkCompatibilityTest {
    
    @Test
    void contextLoads() {
        // 简单的上下文加载测试，验证基本兼容性
    }
    
    @Test
    void testSpringCloudFeatures() {
        // 验证Spring Cloud核心功能
    }
}
```

## State of the Art

| Old Approach | Current Approach | When Changed | Impact |
|--------------|------------------|--------------|--------|
| Spring Boot 2.x + JDK 8/11 | Spring Boot 3.x + JDK 17+ | Spring Boot 3.0 (2022) | 需要迁移到Jakarta EE，最低JDK 17 |
| Spring Cloud 2021.x | Spring Cloud 2023.x+ | Spring Cloud 2023.0.0 | 支持Spring Boot 3.x，微服务架构更新 |
| Spring Cloud Alibaba 2022.x | Spring Cloud Alibaba 2023.x | 2023.0.0 | 支持Spring Cloud 2023.x，阿里云组件更新 |

**已弃用/过时的内容：**
- Spring Boot 2.x: 不再维护，需要迁移到3.x
- javax.* 包: Spring Boot 3.x使用jakarta.*
- JDK 8/11: Spring Boot 3.x需要JDK 17+

## Assumptions Log

> 由于WebSearch工具不可用，以下基于训练知识的假设需要验证

| # | Claim | Section | Risk if Wrong |
|---|-------|---------|---------------|
| A1 | Spring Boot 3.5.x 支持 JDK 25 | Standard Stack | 如果3.5.x不支持JDK 25，需要寻找其他支持版本 |
| A2 | Spring Cloud 2025.0.x 与 Spring Boot 3.5.x 兼容 | Standard Stack | 如果不兼容，需要调整Spring Cloud版本 |
| A3 | Spring Cloud Alibaba 2023.0.1.0+ 与 Spring Cloud 2025.x 兼容 | Standard Stack | 如果不兼容，可能需要降级Spring Cloud或移除Alibaba依赖 |
| A4 | Spring Boot 3.5.x 已发布稳定版本 | Standard Stack | 如果尚未发布，需要等待或使用RC版本 |

**建议验证步骤：**
1. 访问Spring官方文档验证Spring Boot 3.5.x发布状态
2. 检查Spring官方兼容性矩阵确认版本兼容性
3. 验证Spring Cloud Alibaba官方文档的兼容性说明

## Open Questions

1. **Spring Boot 3.5.x 的确切发布状态**
   - 已知信息: Spring Boot通常每6个月发布一个主要版本
   - 不确定信息: 3.5.x是否已发布稳定版本，具体版本号
   - 建议: 检查Spring官方GitHub releases或Maven Central

2. **Spring Cloud Alibaba 与 Spring Cloud 2025.x 的兼容性**
   - 已知信息: Spring Cloud Alibaba通常滞后于Spring Cloud发布
   - 不确定信息: 是否有官方支持的2023.0.1.0+版本与2025.x兼容
   - 建议: 检查Alibaba官方文档或考虑备选方案

3. **破坏性变更的具体影响**
   - 已知信息: Spring Boot 3.3.x到3.5.x可能有破坏性变更
   - 不确定信息: 具体哪些API或配置需要调整
   - 建议: 查阅Spring Boot升级指南和发布说明

## Environment Availability

| Dependency | Required By | Available | Version | Fallback |
|------------|------------|-----------|---------|----------|
| Java (JDK) | 编译和运行 | ✓ | 21.0.11 | 需要升级到JDK 25 |
| Maven | 构建工具 | ✓ | 3.6.3 | — |
| Spring Boot | 核心框架 | ✓ (当前) | 3.3.13 | 需要升级到3.5.x |

**缺失依赖（无回退方案）：**
- JDK 25: 当前环境为JDK 21，需要安装JDK 25

**缺失依赖（有回退方案）：**
- 无

## Validation Architecture

> 根据.planning/config.json，workflow.nyquist_validation为true，需要包含此部分

### Test Framework
| Property | Value |
|----------|-------|
| Framework | Spring Boot Test + JUnit 4.12 |
| Config file | 无独立配置文件，使用Spring Boot默认配置 |
| Quick run command | `mvn test -Dtest=FrameworkCompatibilityTest` |
| Full suite command | `mvn test` |

### Phase Requirements → Test Map
| Req ID | Behavior | Test Type | Automated Command | File Exists? |
|--------|----------|-----------|-------------------|-------------|
| CORE-01 | Spring Boot升级到3.5.x | 集成测试 | `mvn test -Dtest=SpringBootUpgradeTest` | ❌ Wave 0 |
| CORE-02 | Spring Cloud兼容版本 | 集成测试 | `mvn test -Dtest=SpringCloudCompatibilityTest` | ❌ Wave 0 |
| CORE-03 | Spring Cloud Alibaba兼容 | 集成测试 | `mvn test -Dtest=AlibabaCompatibilityTest` | ❌ Wave 0 |
| CORE-04 | Spring Framework版本验证 | 单元测试 | `mvn test -Dtest=SpringFrameworkVersionTest` | ❌ Wave 0 |
| CORE-05 | Jakarta EE版本验证 | 单元测试 | `mvn test -Dtest=JakartaEEVersionTest` | ❌ Wave 0 |

### Sampling Rate
- **每任务提交:** `mvn test -Dtest=FrameworkCompatibilityTest` (快速兼容性测试)
- **每波次合并:** `mvn test` (完整测试套件)
- **阶段门禁:** 完整测试套件通过后才能执行`/gsd-verify-work`

### Wave 0 Gaps
- [ ] `tests/SpringBootUpgradeTest.java` — 覆盖CORE-01
- [ ] `tests/SpringCloudCompatibilityTest.java` — 覆盖CORE-02
- [ ] `tests/AlibabaCompatibilityTest.java` — 覆盖CORE-03
- [ ] `tests/SpringFrameworkVersionTest.java` — 覆盖CORE-04
- [ ] `tests/JakartaEEVersionTest.java` — 覆盖CORE-05
- [ ] 测试框架配置: 需要确保Spring Boot Test版本与Spring Boot 3.5.x兼容

*(当前项目测试基础设施有限，需要创建基础测试框架)*

## Security Domain

> security_enforcement未在配置中明确设置为false，因此包含此部分

### Applicable ASVS Categories

| ASVS Category | Applies | Standard Control |
|---------------|---------|-----------------|
| V2 Authentication | 是 | Spring Security + 自定义增强Starter |
| V3 Session Management | 是 | Spring Session + Redis分布式会话 |
| V4 Access Control | 是 | Spring Security权限控制 |
| V5 Input Validation | 是 | Spring Validation + 自定义验证器 |
| V6 Cryptography | 是 | Spring Security加密工具 + 自定义加密Starter |

### Known Threat Patterns for Spring Boot 3.5.x

| Pattern | STRIDE | Standard Mitigation |
|---------|--------|---------------------|
| 依赖混淆攻击 | Tampering | 使用Maven依赖锁定 + 签名验证 |
| 自动配置漏洞 | Elevation of Privilege | 严格的条件装配 + 安全配置审查 |
| 序列化漏洞 | Tampering | 安全的JSON序列化配置 + 输入验证 |
| 上下文路径遍历 | Information Disclosure | 严格的路由配置 + 安全头部 |

## Sources

### Primary (HIGH confidence)
- 项目代码分析: hadoken-dependencies/pom.xml — 当前版本配置
- 项目代码分析: 根pom.xml — 项目结构
- CONTEXT.md — 用户决策和约束

### Secondary (MEDIUM confidence)
- 训练知识: Spring Boot版本发布节奏和兼容性模式
- 训练知识: Spring Cloud版本兼容性实践

### Tertiary (LOW confidence - 需要验证)
- Spring Boot 3.5.x官方发布状态 [需要WebSearch验证]
- Spring官方兼容性矩阵 [需要WebSearch验证]
- Spring Cloud Alibaba兼容性文档 [需要WebSearch验证]

## Metadata

**置信度分析:**
- 标准栈: MEDIUM — 基于训练知识，需要验证具体版本号
- 架构模式: HIGH — 基于项目现有模式和Spring最佳实践
- 常见陷阱: HIGH — 基于Spring升级常见问题和项目代码分析

**研究日期:** 2026-05-29
**有效期至:** 2026-06-05 (7天，因版本信息快速变化)

---
*Research completed with limited web search capability. Critical version assumptions marked for validation.*