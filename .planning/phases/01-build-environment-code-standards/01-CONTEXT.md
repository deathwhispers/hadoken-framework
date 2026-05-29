# Phase 1: Build Environment & Code Standards - Context

**Gathered:** 2026-05-29
**Status:** Ready for planning

<domain>
## Phase Boundary

建立支持 JDK 25 的构建环境，升级 Maven 和 Maven Compiler Plugin 到兼容版本，并制定 JDK 25 新特性代码规范文档供开发团队使用。

**Requirements covered:** BUILD-01, BUILD-02, BUILD-03, BUILD-04, STD-01, STD-02, STD-03, STD-04

**In scope:**
- Maven 升级至 3.9.x
- Maven Compiler Plugin 升级至 3.14.x
- JDK 版本配置更新为 25
- Maven Resource Plugin 版本更新
- JDK 25 新特性代码规范文档制定

**Out of scope:**
- 实际代码修改（仅配置变更）
- 依赖版本升级（Phase 3-7）
- 安全修复（Phase 2）
- 功能验证（Phase 8）

</domain>

<decisions>
## Implementation Decisions

### JDK 25 新特性代码规范内容
- **D-01:** 规范文档包含四大主题：Virtual Threads、Pattern Matching、Scoped Values、Value Types
- **D-02:** 每个主题包含：概念说明、使用场景、代码示例、注意事项
- **D-03:** 规范强调稳定性优先，不启用 Preview Features，仅使用 JDK 25 正式特性
- **D-04:** Virtual Threads 使用指南：适用于 I/O 密集型任务，不适用于 CPU 密集型任务
- **D-05:** Pattern Matching 使用指南：优先用于记录类解构和 switch 表达式
- **D-06:** Scoped Values 使用指南：替代 ThreadLocal 和 InheritableThreadLocal
- **D-07:** Value Types 使用指南：等待正式发布后引入，当前仅作为前瞻性说明

### 规范文档位置与格式
- **D-08:** 规范文档存放位置：`.planning/docs/JDK25-CODE-STANDARDS.md`
- **D-09:** 文档格式：Markdown，便于阅读和版本控制
- **D-10:** 文档结构：概述 → 各主题章节 → 最佳实践 → 参考资源

### Maven 配置
- **D-11:** Maven Compiler Plugin 版本：3.14.0（支持 JDK 25）
- **D-12:** Maven Resource Plugin 版本：3.3.1（保持，兼容）
- **D-13:** Maven Source Plugin 版本：3.3.0（保持，兼容）
- **D-14:** Java version 属性：25
- **D-15:** Maven 要求版本：3.9.x（开发者需升级本地 Maven）

### Preview Features
- **D-16:** 不启用 JDK 25 Preview Features，仅使用正式稳定特性
- **D-17:** 如需使用预览特性，需在后续阶段单独讨论并添加 `--enable-preview` 参数

### Claude's Discretion
- 规范文档的具体代码示例可基于 JDK 官方文档和 Spring Boot 最佳实践编写
- Maven 配置变更的具体实现细节可按标准 Spring Boot 项目配置模式处理

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Project Context
- `.planning/PROJECT.md` — 项目目标和约束
- `.planning/REQUIREMENTS.md` — 需求定义和映射
- `.planning/ROADMAP.md` — 阶段结构和成功标准
- `.planning/research/SUMMARY.md` — 研究发现摘要
- `.planning/research/STACK.md` — 技术栈版本推荐
- `.planning/research/FEATURES.md` — JDK 25 新特性说明

### Codebase Context
- `.planning/codebase/STACK.md` — 当前技术栈分析
- `.planning/codebase/ARCHITECTURE.md` — 系统架构分析
- `.planning/codebase/CONVENTIONS.md` — 现有代码规范
- `.planning/codebase/STRUCTURE.md` — 项目结构

### Key Files to Modify
- `pom.xml` — 根 POM 文件，java.version 属性
- `hadoken-dependencies/pom.xml` — 依赖版本管理 BOM
- 各模块 `pom.xml` — Maven 插件版本继承

</canonical_refs>

<code_context>
## Existing Code Insights

### Reusable Assets
- 现有 `pom.xml` 结构：已有 `<java.version>17</java.version>` 属性，直接替换为 25
- 现有 Maven 插件配置：已有 `maven-compiler-plugin.version` 属性，直接更新
- 现有代码规范：`.planning/codebase/CONVENTIONS.md` 提供现有命名和风格规范，作为 JDK 25 规范的补充

### Established Patterns
- Maven 属性管理模式：版本属性集中在 `hadoken-dependencies/pom.xml`
- Spring Boot Starter 结构：各模块使用标准目录结构，升级不影响结构
- Javadoc 规范：现有代码使用 `@author yanggj`、`@version 1.0.0`、`@date` 格式

### Integration Points
- 根 `pom.xml` 的 `<properties>` 需要更新 `java.version`
- 各模块继承根 POM 的版本配置，无需单独修改
- CI/CD 环境（如有）需要更新 JDK 版本

</code_context>

<specifics>
## Specific Ideas

用户明确要求：
- 激进升级策略，不考虑 JDK 17 兼容性
- 引入 JDK 25 新特性代码规范
- 规范作为项目上下文供后续开发参考

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope.

</deferred>

---

*Phase: 01-Build Environment & Code Standards*
*Context gathered: 2026-05-29*