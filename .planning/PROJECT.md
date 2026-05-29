# Hadoken Framework

## What This Is

企业级 Spring Boot Starter 集合框架，提供 Web、Security、MyBatis、Redis、MQ、Scheduler、WebSocket、Monitor 等模块的增强功能，简化业务应用开发。

**升级目标:** 将 JDK 从 17 升级到 25，Spring Boot 升级到匹配的稳定版本，引入 JDK 25 新特性代码规范。

## Core Value

提供开箱即用的企业级 Spring Boot Starter 模块，让业务应用只需引入依赖即可获得完整的基础功能支持。

## Requirements

### Validated

- ✓ Spring Boot 3.3.13 + Java 17 多模块架构 — 现有实现
- ✓ 14个 Starter 模块（Web、Security、MyBatis、Redis、MQ、Scheduler 等） — 现有实现
- ✓ 统一响应封装 CommonResult + 全局异常处理 — 现有实现
- ✓ MyBatis-Plus 增强（条件查询扩展、字段加密） — 现有实现
- ✓ Redis 增强（缓存、序列化、工具类） — 现有实现
- ✓ 消息队列封装（Redis Pub/Sub） — 现有实现
- ✓ 任务调度（多存储支持） — 现有实现
- ✓ WebSocket（STOMP） — 现有实现
- ✓ 监控追踪（SkyWalking） — 现有实现
- ✓ MQTT 集成 — 现有实现
- ✓ API 日志/加密/脱敏/XSS防护 — 现有实现

### Active

- [ ] JDK 版本升级至 JDK 25
- [ ] Spring Boot 升级至 JDK 25 兼容的稳定版本
- [ ] 所有依赖版本升级至兼容版本
- [ ] 制定 JDK 25 新特性代码规范
- [ ] 验证所有模块在新版本下正常工作
- [ ] 修复升级过程中的兼容性问题

### Out of Scope

- 业务应用迁移指导 — 暂不提供，后续可单独文档化
- 新功能开发 — 本次升级仅关注版本迁移，不添加新功能
- 性能优化 — 本次升级仅关注兼容性，不做额外性能优化

## Context

**技术背景:**
- 当前 JDK: 17（实际运行 JDK 21）
- 当前 Spring Boot: 3.3.13
- 当前 Spring Cloud: 2025.0.0
- 多模块 Maven 项目，14个子模块
- 依赖版本统一通过 hadoken-dependencies BOM 管理

**升级策略:** 激进升级，不考虑旧版本向后兼容

**代码规范:** 引入 JDK 25 新特性规范（记录类模式匹配、值类型等）

**已知问题:**
- 硬编码加密密钥（安全风险）
- 弱 RSA 密钥（1024位）
- 几乎无单元测试

## Constraints

- **JDK 版本:** 必须升级到 JDK 25
- **Spring Boot 版本:** 必须是支持 JDK 25 的稳定版本
- **兼容性:** 不需要保持 JDK 17 兼容性
- **模块依赖:** Starter 模块可依赖 hadoken-common，不可相互依赖

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| 激进升级策略 | 用户明确要求不考虑旧版本兼容，简化升级过程 | — Pending |
| 引入 JDK 25 新特性规范 | 利用最新语言特性提升代码质量 | — Pending |

---
*Last updated: 2026-05-29 after initialization*

## Evolution

This document evolves at phase transitions and milestone boundaries.

**After each phase transition** (via `/gsd-transition`):
1. Requirements invalidated? → Move to Out of Scope with reason
2. Requirements validated? → Move to Validated with phase reference
3. New requirements emerged? → Add to Active
4. Decisions to log? → Add to Key Decisions
5. "What This Is" still accurate? → Update if drifted

**After each milestone** (via `/gsd-complete-milestone`):
1. Full review of all sections
2. Core Value check — still the right priority?
3. Audit Out of Scope — reasons still valid?
4. Update Context with current state