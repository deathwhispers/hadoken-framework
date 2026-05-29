# Research Summary: Hadoken Framework - JDK 25 + Spring Boot Upgrade

**Domain:** Spring Boot Starter Framework Upgrade
**Researched:** 2026-05-29
**Overall confidence:** MEDIUM (网络访问受限，关键版本信息需要验证)

## Executive Summary

本研究分析了 Hadoken Framework 从 JDK 17 + Spring Boot 3.3.13 升级到 JDK 25 + 兼容 Spring Boot 版本的技术栈。

**核心发现:**

1. **JDK 25 升级路径清晰** - JDK 25 是 2025 年 9 月发布的 LTS 版本（需验证），支持周期到 2032 年。从 JDK 17 直接跳到 JDK 25 是可行的激进升级策略。

2. **Spring Boot 版本选择关键** - Spring Boot 3.5.x 是支持 JDK 25 的推荐版本（需验证官方兼容矩阵）。Spring Boot 3.x 系列已经支持 JDK 17+，JDK 25 兼容性预期良好。

3. **依赖兼容性整体良好** - 大部分核心依赖（MyBatis-Plus、Redisson、Lombok、MapStruct）已有 JDK 21+ 兼容版本，JDK 25 升级风险可控。主要风险点在于 Spring Cloud Alibaba 版本匹配和 Hutool 6.x 破坏性变更。

4. **构建工具需要升级** - Maven 需要 3.9.x 版本以支持 JDK 25，Maven Compiler Plugin 需要 3.14.x。

## Key Findings

**Stack:** JDK 25 (LTS) + Spring Boot 3.5.x + Spring Cloud 2025.0.x + Spring Cloud Alibaba 2023.0.3.x

**Architecture:** 保持现有 Spring Boot Starter 模块化架构，无需架构变更

**Critical pitfall:** Spring Cloud Alibaba 版本与 Spring Boot 版本强绑定，版本不匹配会导致微服务功能失效

## Implications for Roadmap

Based on research, suggested phase structure:

1. **Phase 1: Build Environment Upgrade**
   - Maven 升级到 3.9.x
   - JDK 升级到 25
   - 更新 CI/CD 环境
   - Addresses: 基础构建能力
   - Avoids: 编译工具版本不匹配导致的构建失败

2. **Phase 2: Core Framework Upgrade**
   - Spring Boot 升级到 3.5.x
   - Spring Cloud 升级到 2025.0.x
   - Spring Cloud Alibaba 升级到兼容版本
   - Addresses: 核心框架能力
   - Pitfall: 版本兼容性验证至关重要

3. **Phase 3: Dependencies Upgrade**
   - MyBatis-Plus、Redisson 等核心依赖升级
   - SpringDoc、Knife4j API 文档工具升级
   - Addresses: 第三方依赖兼容性
   - Pitfall: Hutool 6.x 可能有破坏性变更

4. **Phase 4: JDK 25 Features Adoption**
   - 引入记录类模式匹配
   - 虚拟线程应用
   - 值类型探索
   - Addresses: 代码质量提升
   - Note: 需要 JDK 25 新特性规范

**Phase ordering rationale:**
- 构建工具先行，确保编译能力
- 核心框架其次，奠定基础
- 依赖升级随后，解决兼容性
- 新特性最后，渐进式引入

**Research flags for phases:**
- Phase 2: 需要深入研究 Spring Boot 3.5.x 破坏性变更
- Phase 3: 需要深入研究 Hutool 6.x 迁移指南
- Phase 4: 需要深入研究 JDK 25 新特性和最佳实践

## Confidence Assessment

| Area | Confidence | Notes |
|------|------------|-------|
| JDK 25 LTS Status | MEDIUM | 基于 JDK 发布周期推断，需验证 |
| Spring Boot Version | MEDIUM | 基于历史版本模式推断，需验证官方矩阵 |
| Spring Cloud Version | MEDIUM | 依赖 Spring Boot 版本，需验证 |
| Spring Cloud Alibaba | LOW | 阿里云发布节奏不透明，必须验证 |
| Dependencies | HIGH | 大多数依赖持续更新，JDK 兼容性好 |
| Build Tools | HIGH | Maven 3.9.x 已发布，JDK 25 支持 |

## Gaps to Address

- **Spring Boot 3.5.x 发布状态** - 需要访问 Spring 官方网站确认
- **Spring Cloud Alibaba 兼容版本** - 需要访问 GitHub 确认
- **JDK 25 正式发布和特性列表** - 需要访问 OpenJDK 确认
- **Hutool 6.x 稳定性** - 需要访问 GitHub/Hutool 官网确认
- **SpringDoc 2.8.x 兼容性** - 需要验证与 Spring Boot 3.5.x 的兼容性

## Recommended Actions

### Immediate (验证前)

1. 验证 Spring Boot 3.5.x 发布状态
2. 验证 Spring Cloud Alibaba 版本兼容性
3. 确认 JDK 25 正式发布

### Before Upgrade

1. 创建新分支进行升级测试
2. 准备回滚方案
3. 更新 CI/CD 环境

### During Upgrade

1. 按推荐顺序逐步升级
2. 每个阶段运行完整测试
3. 记录所有破坏性变更

---

*Research completed: 2026-05-29*
*Note: 由于网络访问限制，本研究中标注为 MEDIUM/LOW 置信度的信息需要在执行前通过官方渠道验证*