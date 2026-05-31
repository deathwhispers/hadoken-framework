# Requirements: Hadoken Framework - JDK 25 + Spring Boot Upgrade

**Defined:** 2026-05-29
**Core Value:** 提供开箱即用的企业级 Spring Boot Starter 模块

## v1 Requirements

升级需求按类别分组。

### Build Environment

- [x] **BUILD-01**: Maven 升级至 3.9.x 以支持 JDK 25 ✅
- [x] **BUILD-02**: Maven Compiler Plugin 升级至 3.14.x ✅
- [x] **BUILD-03**: JDK 版本更新为 25 ✅
- [x] **BUILD-04**: Maven Resource Plugin 升级至兼容版本 ✅

### Core Framework

- [x] **CORE-01**: Spring Boot 升级至 3.5.x（或 JDK 25 兼容的最新稳定版） ✅ (3.5.5)
- [x] **CORE-02**: Spring Cloud 升级至兼容版本 ✅ (2025.0.0)
- [x] **CORE-03**: Spring Cloud Alibaba 升级至兼容版本 ✅ (2023.0.1.0)
- [x] **CORE-04**: Spring Framework 版本验证（随 Spring Boot BOM） ✅
- [x] **CORE-05**: Jakarta EE 版本验证（随 Spring Boot BOM） ✅

### Database & ORM

- [x] **DB-01**: MyBatis-Plus 版本验证/升级至 JDK 25 兼容版本 ✅ (3.5.12)
- [x] **DB-02**: Druid 连接池版本验证/升级 ✅ (1.2.24)
- [x] **DB-03**: MySQL Connector 版本验证/升级 ✅ (8.4.0)
- [x] **DB-04**: Dynamic Datasource 版本验证/升级 ✅ (未使用)

### Redis & Cache

- [x] **REDIS-01**: Redisson 升级至 JDK 25 兼容版本 ✅ (3.45.1)
- [x] **REDIS-02**: Spring Data Redis 版本验证（随 Spring Boot） ✅

### Tools & Utilities

- [x] **UTIL-01**: Hutool 版本决策（5.x 保持或 6.x 升级） ✅ (保持 5.8.39)
- [x] **UTIL-02**: Lombok 版本验证/升级 ✅ (1.18.46)
- [x] **UTIL-03**: MapStruct 版本验证/升级 ✅ (1.6.3)
- [x] **UTIL-04**: Guava 版本验证/升级 ✅ (33.4.8-jre)
- [x] **UTIL-05**: Fastjson2 版本验证/升级 ✅ (2.0.57)

### API Documentation

- [ ] **API-01**: SpringDoc 升级至 Spring Boot 3.5.x 兼容版本 (编译验证通过 2.6.0)
- [ ] **API-02**: Knife4j 升级至兼容版本 (编译验证通过 4.5.0)

### Monitoring

- [ ] **MON-01**: SkyWalking 版本验证/升级（Agent JDK 25 支持）(编译验证通过 9.0.0)
- [ ] **MON-02**: Spring Boot Admin 版本升级 (编译验证通过 3.3.1)

### Security

- [x] **SEC-01**: 移除硬编码加密密钥（改为配置注入） ✅
- [x] **SEC-02**: 替换 DES 加密算法为 AES-256 ✅
- [x] **SEC-03**: RSA 密钥长度升级至 2048 位或以上 ✅
- [x] **SEC-04**: 默认密码移除/改为配置 ✅

### Code Standards

- [x] **STD-01**: 制定 JDK 25 新特性代码规范文档 ✅
- [x] **STD-02**: 记录类模式匹配使用规范 ✅
- [x] **STD-03**: 虚拟线程使用规范 ✅
- [x] **STD-04**: 值类型使用规范（如适用） ✅

### Verification

- [ ] **VERIFY-01**: 所有模块编译通过 (✅ 已完成，待文档更新)
- [ ] **VERIFY-02**: 所有模块单元测试通过（需先创建测试）
- [ ] **VERIFY-03**: 每个模块功能验证通过

## v2 Requirements

后续可选需求，不在本次升级范围。

### New Features

- **NEW-01**: 引入值类型优化关键数据结构
- **NEW-02**: 全面采用虚拟线程替换传统线程池
- **NEW-03**: 结构化并发模式应用

### Performance

- **PERF-01**: GC 参数调优（ZGC/Shenandoah）
- **PERF-02**: 启动时间优化
- **PERF-03**: 内存占用优化

### Testing

- **TEST-01**: 创建完整单元测试覆盖
- **TEST-02**: 创建集成测试框架
- **TEST-03**: 创建性能基准测试

## Out of Scope

| Feature | Reason |
|---------|--------|
| 业务应用迁移指导 | 暂不提供，后续可单独文档化 |
| 新功能开发 | 本次升级仅关注版本迁移 |
| 性能优化 | 本次仅关注兼容性，不做额外优化 |
| JPMS 模块系统引入 | Spring Boot 生态支持有限，风险高 |
| Gradle 迁移 | 项目已使用 Maven，迁移成本高 |

## Traceability

| Requirement | Phase | Status |
|-------------|-------|--------|
| BUILD-01 | Phase 1 | ✅ Complete |
| BUILD-02 | Phase 1 | ✅ Complete |
| BUILD-03 | Phase 1 | ✅ Complete |
| BUILD-04 | Phase 1 | ✅ Complete |
| STD-01 | Phase 1 | ✅ Complete |
| STD-02 | Phase 1 | ✅ Complete |
| STD-03 | Phase 1 | ✅ Complete |
| STD-04 | Phase 1 | ✅ Complete |
| SEC-01 | Phase 2 | ✅ Complete |
| SEC-02 | Phase 2 | ✅ Complete |
| SEC-03 | Phase 2 | ✅ Complete |
| SEC-04 | Phase 2 | ✅ Complete |
| CORE-01 | Phase 3 | ✅ Complete |
| CORE-02 | Phase 3 | ✅ Complete |
| CORE-03 | Phase 3 | ✅ Complete |
| CORE-04 | Phase 3 | ✅ Complete |
| CORE-05 | Phase 3 | ✅ Complete |
| DB-01 | Phase 4 | ✅ Complete |
| DB-02 | Phase 4 | ✅ Complete |
| DB-03 | Phase 4 | ✅ Complete |
| DB-04 | Phase 4 | ✅ Complete (未使用) |
| REDIS-01 | Phase 4 | ✅ Complete |
| REDIS-02 | Phase 4 | ✅ Complete |
| UTIL-01 | Phase 5 | ✅ Complete |
| UTIL-02 | Phase 5 | ✅ Complete |
| UTIL-03 | Phase 5 | ✅ Complete |
| UTIL-04 | Phase 5 | ✅ Complete |
| UTIL-05 | Phase 5 | ✅ Complete |
| API-01 | Phase 6 | ⏳ 编译验证通过 |
| API-02 | Phase 6 | ⏳ 编译验证通过 |
| MON-01 | Phase 7 | ⏳ 编译验证通过 |
| MON-02 | Phase 7 | ⏳ 编译验证通过 |
| VERIFY-01 | Phase 8 | ✅ 编译通过 |
| VERIFY-02 | Phase 8 | 📋 Pending |
| VERIFY-03 | Phase 8 | 📋 Pending |

**Coverage:**
- v1 requirements: 35 total
- ✅ Complete: 28
- ⏳ 编译验证通过: 4
- 📋 Pending: 3
- Unmapped: 0 ✓

---
*Requirements defined: 2026-05-29*
*Last updated: 2026-05-29 after initial definition*