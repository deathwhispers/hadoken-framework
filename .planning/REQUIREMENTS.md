# Requirements: Hadoken Framework - JDK 25 + Spring Boot Upgrade

**Defined:** 2026-05-29
**Core Value:** 提供开箱即用的企业级 Spring Boot Starter 模块

## v1 Requirements

升级需求按类别分组。

### Build Environment

- [ ] **BUILD-01**: Maven 升级至 3.9.x 以支持 JDK 25
- [ ] **BUILD-02**: Maven Compiler Plugin 升级至 3.14.x
- [ ] **BUILD-03**: JDK 版本更新为 25
- [ ] **BUILD-04**: Maven Resource Plugin 升级至兼容版本

### Core Framework

- [ ] **CORE-01**: Spring Boot 升级至 3.5.x（或 JDK 25 兼容的最新稳定版）
- [ ] **CORE-02**: Spring Cloud 升级至兼容版本
- [ ] **CORE-03**: Spring Cloud Alibaba 升级至兼容版本
- [ ] **CORE-04**: Spring Framework 版本验证（随 Spring Boot BOM）
- [ ] **CORE-05**: Jakarta EE 版本验证（随 Spring Boot BOM）

### Database & ORM

- [ ] **DB-01**: MyBatis-Plus 版本验证/升级至 JDK 25 兼容版本
- [ ] **DB-02**: Druid 连接池版本验证/升级
- [ ] **DB-03**: MySQL Connector 版本验证/升级
- [ ] **DB-04**: Dynamic Datasource 版本验证/升级

### Redis & Cache

- [ ] **REDIS-01**: Redisson 升级至 JDK 25 兼容版本
- [ ] **REDIS-02**: Spring Data Redis 版本验证（随 Spring Boot）

### Tools & Utilities

- [ ] **UTIL-01**: Hutool 版本决策（5.x 保持或 6.x 升级）
- [ ] **UTIL-02**: Lombok 版本验证/升级
- [ ] **UTIL-03**: MapStruct 版本验证/升级
- [ ] **UTIL-04**: Guava 版本验证/升级
- [ ] **UTIL-05**: Fastjson2 版本验证/升级

### API Documentation

- [ ] **API-01**: SpringDoc 升级至 Spring Boot 3.5.x 兼容版本
- [ ] **API-02**: Knife4j 升级至兼容版本

### Monitoring

- [ ] **MON-01**: SkyWalking 版本验证/升级（Agent JDK 25 支持）
- [ ] **MON-02**: Spring Boot Admin 版本升级

### Security

- [ ] **SEC-01**: 移除硬编码加密密钥（改为配置注入）
- [ ] **SEC-02**: 替换 DES 加密算法为 AES-256
- [ ] **SEC-03**: RSA 密钥长度升级至 2048 位或以上
- [ ] **SEC-04**: 默认密码移除/改为配置

### Code Standards

- [ ] **STD-01**: 制定 JDK 25 新特性代码规范文档
- [ ] **STD-02**: 记录类模式匹配使用规范
- [ ] **STD-03**: 虚拟线程使用规范
- [ ] **STD-04**: 值类型使用规范（如适用）

### Verification

- [ ] **VERIFY-01**: 所有模块编译通过
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
| BUILD-01 | Phase 1 | Pending |
| BUILD-02 | Phase 1 | Pending |
| BUILD-03 | Phase 1 | Pending |
| BUILD-04 | Phase 1 | Pending |
| STD-01 | Phase 1 | Pending |
| STD-02 | Phase 1 | Pending |
| STD-03 | Phase 1 | Pending |
| STD-04 | Phase 1 | Pending |
| SEC-01 | Phase 2 | Pending |
| SEC-02 | Phase 2 | Pending |
| SEC-03 | Phase 2 | Pending |
| SEC-04 | Phase 2 | Pending |
| CORE-01 | Phase 3 | Pending |
| CORE-02 | Phase 3 | Pending |
| CORE-03 | Phase 3 | Pending |
| CORE-04 | Phase 3 | Pending |
| CORE-05 | Phase 3 | Pending |
| DB-01 | Phase 4 | Pending |
| DB-02 | Phase 4 | Pending |
| DB-03 | Phase 4 | Pending |
| DB-04 | Phase 4 | Pending |
| REDIS-01 | Phase 4 | Pending |
| REDIS-02 | Phase 4 | Pending |
| UTIL-01 | Phase 5 | Pending |
| UTIL-02 | Phase 5 | Pending |
| UTIL-03 | Phase 5 | Pending |
| UTIL-04 | Phase 5 | Pending |
| UTIL-05 | Phase 5 | Pending |
| API-01 | Phase 6 | Pending |
| API-02 | Phase 6 | Pending |
| MON-01 | Phase 7 | Pending |
| MON-02 | Phase 7 | Pending |
| VERIFY-01 | Phase 8 | Pending |
| VERIFY-02 | Phase 8 | Pending |
| VERIFY-03 | Phase 8 | Pending |

**Coverage:**
- v1 requirements: 35 total
- Mapped to phases: 35
- Unmapped: 0 ✓

---
*Requirements defined: 2026-05-29*
*Last updated: 2026-05-29 after initial definition*