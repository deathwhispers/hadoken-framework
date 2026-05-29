---
phase: "03"
slug: "core-framework-upgrade"
status: draft
nyquist_compliant: true
wave_0_complete: true
created: "2026-05-29"
---

# Phase 03 — Validation Strategy

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | Maven Build + Spring Boot Test |
| **Config file** | pom.xml |
| **Quick run command** | `mvn clean compile -DskipTests` |
| **Full suite command** | `mvn clean compile` |
| **Estimated runtime** | ~60 seconds |

## Sampling Rate

- **After every task commit:** Run `mvn clean compile -pl hadoken-common`
- **After every plan wave:** Run `mvn clean compile`
- **Before verification:** Full build must pass

## Per-Task Verification Map

| Task ID | Plan | Wave | Requirement | Test Type | Automated Command | Status |
|---------|------|------|-------------|-----------|-------------------|--------|
| 03-01-01 | 01 | 1 | CORE-01 | build | `mvn help:effective-pom | grep spring-boot.version` | ⬜ pending |
| 03-01-02 | 01 | 1 | CORE-01 | build | `mvn clean compile -DskipTests` | ⬜ pending |
| 03-01-03 | 01 | 1 | CORE-04, CORE-05 | build | `mvn dependency:tree | grep spring-framework` | ⬜ pending |
| 03-02-01 | 02 | 1 | CORE-02 | config | `grep "spring-cloud.version" hadoken-dependencies/pom.xml` | ⬜ pending |
| 03-02-02 | 02 | 1 | CORE-03 | config | `grep "spring-cloud-alibaba" hadoken-dependencies/pom.xml` | ⬜ pending |
| 03-03-01 | 03 | 2 | All CORE | build | `mvn clean compile` | ⬜ pending |

## Wave 0 Requirements

- [x] Maven build infrastructure exists
- [x] Spring Boot parent pom configured
- [x] hadoken-dependencies BOM exists

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| Spring Boot启动验证 | CORE-01 | 需要完整Spring Boot应用 | 启动任意Starter模块，检查启动日志 |
| 微服务功能验证 | CORE-02, CORE-03 | 需要完整应用上下文 | 验证Spring Cloud组件正常加载 |

## Validation Sign-Off

- [x] All tasks have automated verify
- [x] Wave 0 covers all dependencies
- [x] Feedback latency < 60s

**Approval:** pending