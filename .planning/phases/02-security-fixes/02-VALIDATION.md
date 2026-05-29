---
phase: "02"
slug: "security-fixes"
status: draft
nyquist_compliant: true
wave_0_complete: true
created: "2026-05-29"
---

# Phase 02 — Validation Strategy

> Per-phase validation contract for feedback sampling during execution.

---

## Test Infrastructure

| Property | Value |
|----------|-------|
| **Framework** | JUnit 5 + Mockito |
| **Config file** | pom.xml (添加依赖) |
| **Quick run command** | `mvn test -Dtest=*Test -pl hadoken-common` |
| **Full suite command** | `mvn test -pl hadoken-common,hadoken-security-spring-boot-starter` |
| **Estimated runtime** | ~30 seconds |

---

## Sampling Rate

- **After every task commit:** Run `mvn test -Dtest=*Test -pl hadoken-common`
- **After every plan wave:** Run `mvn test -pl hadoken-common,hadoken-security-spring-boot-starter`
- **Before `/gsd-verify-work`:** Full suite must be green
- **Max feedback latency:** 30 seconds

---

## Per-Task Verification Map

| Task ID | Plan | Wave | Requirement | Threat Ref | Secure Behavior | Test Type | Automated Command | File Exists | Status |
|---------|------|------|-------------|------------|-----------------|-----------|-------------------|-------------|--------|
| 02-01-01 | 01 | 1 | SEC-01 | T-02-01 | AES密钥通过配置注入，无硬编码 | unit | `mvn test -Dtest=EncryptPropertiesTest` | ❌ W0 | ⬜ pending |
| 02-01-02 | 01 | 1 | SEC-01 | T-02-01 | RSA私钥通过配置注入 | unit | `mvn test -Dtest=RsaPropertiesTest` | ❌ W0 | ⬜ pending |
| 02-02-01 | 02 | 1 | SEC-02 | T-02-02 | AES-256-GCM加密解密正常工作 | unit | `mvn test -Dtest=EncryptUtilsTest#testAesEncryption` | ❌ W0 | ⬜ pending |
| 02-02-02 | 02 | 1 | SEC-02 | T-02-02 | DES加密方法抛出弃用警告 | unit | `mvn test -Dtest=EncryptUtilsTest#testDesDeprecated` | ❌ W0 | ⬜ pending |
| 02-03-01 | 03 | 2 | SEC-03 | T-02-03 | RSA密钥长度>=2048位 | unit | `mvn test -Dtest=RsaUtilsTest#testRsaKeyLength` | ❌ W0 | ⬜ pending |
| 02-03-02 | 03 | 2 | SEC-03 | T-02-03 | RSA使用java.util.Base64 | unit | `mvn test -Dtest=RsaUtilsTest#testBase64Api` | ❌ W0 | ⬜ pending |
| 02-04-01 | 04 | 2 | SEC-04 | T-02-04 | SecurityProperties无默认密码 | unit | `mvn test -Dtest=SecurityPropertiesTest` | ❌ W0 | ⬜ pending |
| 02-04-02 | 04 | 2 | SEC-04 | T-02-04 | 配置缺失时拒绝启动 | integration | `mvn test -Dtest=SecurityConfigValidationTest` | ❌ W0 | ⬜ pending |

*Status: ⬜ pending · ✅ green · ❌ red · ⚠️ flaky*

---

## Wave 0 Requirements

- [ ] `hadoken-common/src/test/java/com/github/hadoken/common/util/EncryptUtilsTest.java` — 覆盖SEC-02
- [ ] `hadoken-common/src/test/java/com/github/hadoken/common/util/RsaUtilsTest.java` — 覆盖SEC-03
- [ ] `hadoken-common/src/test/java/com/github/hadoken/common/config/EncryptPropertiesTest.java` — 覆盖SEC-01
- [ ] `hadoken-security-spring-boot-starter/src/test/java/com/github/hadoken/framework/security/autoconfigure/SecurityPropertiesTest.java` — 覆盖SEC-04
- [ ] `pom.xml` — 添加JUnit 5和Mockito依赖

---

## Manual-Only Verifications

| Behavior | Requirement | Why Manual | Test Instructions |
|----------|-------------|------------|-------------------|
| 应用启动验证配置注入 | SEC-01 | 需要完整的Spring Boot上下文 | 启动应用，检查日志是否显示密钥注入成功 |
| 现有数据兼容性 | SEC-02 | 需要真实数据测试 | 检查现有DES加密数据是否能正确迁移或兼容 |

---

## Validation Sign-Off

- [ ] All tasks have `<automated>` verify or Wave 0 dependencies
- [ ] Sampling continuity: no 3 consecutive tasks without automated verify
- [ ] Wave 0 covers all MISSING references
- [ ] No watch-mode flags
- [ ] Feedback latency < 30s
- [ ] `nyquist_compliant: true` set in frontmatter

**Approval:** pending