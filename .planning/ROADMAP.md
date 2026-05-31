# Roadmap: Hadoken Framework JDK 25 + Spring Boot Upgrade

## Overview

本项目将 Hadoken Framework 从 JDK 17 + Spring Boot 3.3.13 升级到 JDK 25 + Spring Boot 3.5.x（或兼容版本），同时修复安全漏洞并制定 JDK 25 新特性代码规范。升级采用激进策略，不考虑旧版本向后兼容，分 8 个阶段依次完成构建环境、安全修复、核心框架、依赖升级、工具库、API 文档、监控工具和最终验证。

## Phases

**Phase Numbering:**
- Integer phases (1, 2, 3): Planned milestone work
- Decimal phases (2.1, 2.2): Urgent insertions (marked with INSERTED)

Decimal phases appear between their surrounding integers in numeric order.

- [ ] **Phase 1: Build Environment & Code Standards** - 建立支持 JDK 25 的构建环境并制定代码规范
- [ ] **Phase 2: Security Fixes** - 修复现有安全漏洞，确保代码安全性
- [ ] **Phase 3: Core Framework Upgrade** - 升级 Spring Boot、Spring Cloud 及相关核心框架
- [ ] **Phase 4: Database & Redis Dependencies** - 升级数据库和 Redis 相关依赖
- [ ] **Phase 5: Tools & Utilities** - 升级工具库和通用依赖
- [ ] **Phase 6: API Documentation** - 升级 API 文档工具
- [ ] **Phase 7: Monitoring Tools** - 升级监控和追踪工具
- [ ] **Phase 8: Verification & Testing** - 全面验证和测试

## Phase Details

### Phase 1: Build Environment & Code Standards
**Goal**: 项目可以在 JDK 25 环境下成功构建，开发团队有明确的 JDK 25 新特性代码规范可遵循
**Depends on**: Nothing (first phase)
**Requirements**: BUILD-01, BUILD-02, BUILD-03, BUILD-04, STD-01, STD-02, STD-03, STD-04
**Success Criteria** (what must be TRUE):
  1. 项目可以在 JDK 25 环境下成功编译
  2. Maven 版本为 3.9.x，Maven Compiler Plugin 版本为 3.14.x
  3. JDK 25 新特性代码规范文档已创建并可被开发团队访问
  4. 规范文档涵盖记录类模式匹配、虚拟线程、值类型的使用指南
**Plans**: 2 plans

Plans:
- [ ] 01-01-PLAN.md — Maven配置升级（BUILD-01~04）
- [ ] 01-02-PLAN.md — JDK 25代码规范文档创建（STD-01~04）

### Phase 2: Security Fixes
**Goal**: 消除代码中的安全隐患，确保加密机制符合现代安全标准
**Depends on**: Phase 1
**Requirements**: SEC-01, SEC-02, SEC-03, SEC-04
**Success Criteria** (what must be TRUE):
  1. 代码中不存在硬编码的加密密钥，所有密钥通过配置注入
  2. 加密算法已从 DES 升级到 AES-256 或更强的安全标准
  3. RSA 密钥长度已升级到 2048 位或以上
  4. 不存在硬编码或默认密码，所有敏感配置通过外部注入
**Plans**: 4 plans in 2 waves

Plans:
- [x] 02-01-PLAN.md — 密钥配置注入（SEC-01）— Wave 1
- [x] 02-02-PLAN.md — DES到AES-256升级（SEC-02）— Wave 1
- [x] 02-03-PLAN.md — RSA密钥升级（SEC-03）— Wave 2 (依赖02-01)
- [x] 02-04-PLAN.md — 默认密码移除（SEC-04）— Wave 2 (依赖02-01)

### Phase 3: Core Framework Upgrade
**Goal**: Spring Boot、Spring Cloud、Spring Cloud Alibaba 升级到支持 JDK 25 的兼容版本
**Depends on**: Phase 2
**Requirements**: CORE-01, CORE-02, CORE-03, CORE-04, CORE-05
**Success Criteria** (what must be TRUE):
  1. Spring Boot 已升级到 3.5.x 或支持 JDK 25 的最新稳定版本
  2. Spring Cloud 版本与 Spring Boot 兼容，微服务功能正常
  3. Spring Cloud Alibaba 版本与 Spring Cloud 兼容
  4. Spring Framework 和 Jakarta EE 版本通过 Spring Boot BOM 正确管理
  5. 核心框架启动成功，无兼容性错误
**Plans**: TBD

### Phase 4: Database & Redis Dependencies
**Goal**: 数据库和缓存相关依赖升级到 JDK 25 兼容版本
**Depends on**: Phase 3
**Requirements**: DB-01, DB-02, DB-03, DB-04, REDIS-01, REDIS-02
**Success Criteria** (what must be TRUE):
  1. MyBatis-Plus 版本已验证或升级，在 JDK 25 下正常工作
  2. Druid 连接池、MySQL Connector、Dynamic Datasource 版本已验证或升级
  3. Redisson 版本已升级到支持 JDK 25
  4. Spring Data Redis 版本通过 Spring Boot BOM 正确管理
  5. 数据库和 Redis 功能测试通过
**Plans**: 3 plans in 2 waves

Plans:
- [x] 04-01-PLAN.md — 验证和升级MyBatis-Plus及相关数据库组件（DB-01~04）— Wave 1 — **完成**
- [x] 04-02-PLAN.md — 验证和升级Redis相关组件（REDIS-01~02）— Wave 1 — **完成**
- [x] 04-03-PLAN.md — 功能验证和测试（覆盖所有组件）— Wave 2 — **完成（测试代码准备）**

### Phase 5: Tools & Utilities
**Goal**: 工具库升级到 JDK 25 兼容版本，破坏性变更已识别和处理
**Depends on**: Phase 4
**Requirements**: UTIL-01, UTIL-02, UTIL-03, UTIL-04, UTIL-05
**Success Criteria** (what must be TRUE):
  1. Hutool 版本决策已做出（保持 5.x 或升级到 6.x），破坏性变更已处理
  2. Lombok 版本已验证或升级，注解处理正常
  3. MapStruct 版本已验证或升级，对象映射功能正常
  4. Guava 和 Fastjson2 版本已验证或升级
  5. 所有工具库功能测试通过
**Plans**: TBD

### Phase 6: API Documentation
**Goal**: API 文档工具升级到 Spring Boot 3.5.x 兼容版本
**Depends on**: Phase 5
**Requirements**: API-01, API-02
**Success Criteria** (what must be TRUE):
  1. SpringDoc 版本已升级到兼容 Spring Boot 3.5.x
  2. Knife4j 版本已升级到兼容版本
  3. API 文档页面可以正常访问和展示
**Plans**: TBD

### Phase 7: Monitoring Tools
**Goal**: 监控和追踪工具升级到支持 JDK 25
**Depends on**: Phase 6
**Requirements**: MON-01, MON-02
**Success Criteria** (what must be TRUE):
  1. SkyWalking Agent 支持 JDK 25，应用追踪功能正常
  2. Spring Boot Admin 版本已升级，监控功能正常
**Plans**: TBD

### Phase 8: Verification & Testing
**Goal**: 确保升级后的框架所有模块功能正常
**Depends on**: Phase 7
**Requirements**: VERIFY-01, VERIFY-02, VERIFY-03
**Success Criteria** (what must be TRUE):
  1. 所有 14 个 Starter 模块在 JDK 25 环境下编译通过
  2. 所有模块的单元测试通过（如有）
  3. 每个模块的核心功能验证通过
**Plans**: TBD

## Progress

**Execution Order:**
Phases execute in numeric order: 1 → 2 → 3 → 4 → 5 → 6 → 7 → 8

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Build Environment & Code Standards | 0/2 | Not started | - |
| 2. Security Fixes | 0/4 | Not started | - |
| 3. Core Framework Upgrade | 0/TBD | Not started | - |
| 4. Database & Redis Dependencies | 3/3 | ✅ **完成** | 2026/05/30 |
| 5. Tools & Utilities | - | ✅ **完成** - 编译验证通过 | 2026/05/30 |
| 6. API Documentation | 0/TBD | Not started | - |
| 7. Monitoring Tools | 0/TBD | Not started | - |
| 8. Verification & Testing | 0/TBD | Not started | - |