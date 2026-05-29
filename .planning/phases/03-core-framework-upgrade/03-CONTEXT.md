# Phase 3: Core Framework Upgrade - Context

**Gathered:** 2026-05-29
**Status:** Ready for planning
**Mode:** auto

<domain>
## Phase Boundary

Spring Boot、Spring Cloud、Spring Cloud Alibaba 升级到支持 JDK 25 的兼容版本，确保核心框架在 JDK 25 环境下正常工作。

**Requirements covered:** CORE-01, CORE-02, CORE-03, CORE-04, CORE-05

**In scope:**
- Spring Boot 升级至 3.5.x
- Spring Cloud 升级至兼容版本
- Spring Cloud Alibaba 升级至兼容版本
- Spring Framework 和 Jakarta EE 版本通过 Spring Boot BOM 管理

**Out of scope:**
- Spring Boot 4.x（未发布）
- 新功能引入
- 微服务架构改造

</domain>

<decisions>
## Implementation Decisions

### Spring Boot 版本
- **D-01:** Spring Boot 版本：3.5.x（JDK 25 兼容的最新稳定版本）
- **D-02:** 升级方式：通过 hadoken-dependencies BOM 统一管理
- **D-03:** 版本属性：`spring-boot.version` → `3.5.0`（或最新稳定版）

### Spring Cloud 版本
- **D-04:** Spring Cloud 版本：2025.0.x（与 Spring Boot 3.5.x 兼容）
- **D-05:** 版本属性：`spring-cloud.version` → `2025.0.0`
- **D-06:** 微服务功能保持现有实现，仅版本升级

### Spring Cloud Alibaba 版本
- **D-07:** Spring Cloud Alibaba 版本：2023.0.1.0+（与 Spring Cloud 2025.x 兼容的版本）
- **D-08:** 如无兼容版本，考虑降级到 Spring Cloud 2023.x 或移除依赖
- **D-09:** 阿里云组件保持现有功能

### 版本管理
- **D-10:** Spring Framework 版本通过 Spring Boot BOM 管理，无需单独指定
- **D-11:** Jakarta EE 版本通过 Spring Boot BOM 管理
- **D-12:** 所有版本变更集中在 hadoken-dependencies/pom.xml

### Claude's Discretion
- 具体版本号根据 Spring 官方兼容性矩阵确定
- 编译失败时按错误信息调整版本
- 遵循 Spring Boot 升级指南处理破坏性变更

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Project Context
- `.planning/PROJECT.md` — 项目目标和约束
- `.planning/REQUIREMENTS.md` — 需求定义（CORE-01~05）
- `.planning/ROADMAP.md` — Phase 03 详情
- `.planning/docs/JDK25-CODE-STANDARDS.md` — JDK 25 规范

### Key Files to Modify
- `hadoken-dependencies/pom.xml` — BOM 依赖版本管理
- `pom.xml` — 根 POM，Spring Boot parent 继承

### Version Compatibility References
- Spring Boot 3.5.x — 支持 JDK 25
- Spring Cloud 2025.0.x — 与 Spring Boot 3.5.x 兼容
- Spring Cloud Alibaba — 需验证兼容性

</canonical_refs>

<code_context>
## Existing Code Insights

### Established Patterns
- BOM 继承模式：`hadoken-dependencies` 管理 all versions
- Spring Boot parent：根 pom.xml 继承 `spring-boot-starter-parent`
- 属性管理：版本通过 `<spring-boot.version>` 等属性定义

### Current Versions
- Spring Boot: 3.3.13
- Spring Cloud: 2025.0.0
- Spring Cloud Alibaba: 2023.0.1.0

</code_context>

<specifics>
## Specific Ideas

激进升级策略，使用最新稳定版本。Spring Boot 3.5.x 是 JDK 25 的官方支持版本。

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope.

</deferred>

---

*Phase: 03-Core Framework Upgrade*
*Context gathered: 2026-05-29 via auto-mode*