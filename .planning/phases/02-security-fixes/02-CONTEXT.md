# Phase 2: Security Fixes - Context

**Gathered:** 2026-05-29
**Status:** Ready for planning

<domain>
## Phase Boundary

消除代码中的安全隐患，确保加密机制符合现代安全标准。修复硬编码密钥、弱加密算法、弱RSA密钥、默认密码等问题。

**Requirements covered:** SEC-01, SEC-02, SEC-03, SEC-04

**In scope:**
- 移除硬编码加密密钥，改为配置注入
- 替换 DES 加密算法为 AES-256
- RSA 密钥长度升级至 2048 位
- 移除默认密码

**Out of scope:**
- 新增加密功能
- 性能优化
- 密钥管理系统建设

</domain>

<decisions>
## Implementation Decisions

### 密钥注入方式
- **D-01:** 密钥注入方式：环境变量 + 配置文件双重支持
- **D-02:** 配置属性前缀：`hadoken.security.encrypt.*`
- **D-03:** 必需配置项：`hadoken.security.encrypt.aes-key`, `hadoken.security.encrypt.rsa-private-key`, `hadoken.security.encrypt.rsa-public-key`
- **D-04:** 启动时验证：缺失必需配置项时抛出异常，拒绝启动

### AES-256 实现
- **D-05:** AES 实现：使用 JDK 内置 AES-256-GCM（无需额外依赖）
- **D-06:** 替换范围：EncryptUtils.java 中的 DES 加密
- **D-07:** API 兼容性：保持现有方法签名，内部实现替换
- **D-08:** 密钥长度：256位（32字节）

### RSA 密钥升级
- **D-09:** RSA 密钥长度：2048位（最小安全标准）
- **D-10:** 密钥来源：外部配置注入，不硬编码
- **D-11:** 替换范围：RsaUtils.java 中的 1024位密钥
- **D-12:** 保留兼容：支持从配置加载已有密钥

### 默认密码移除
- **D-13:** 默认密码策略：完全移除所有硬编码默认密码
- **D-14:** 验证机制：密码必须从配置获取，缺失时拒绝操作

### Claude's Discretion
- 具体代码修改细节按现有代码风格处理
- 错误处理遵循现有 GlobalExceptionHandler 模式

</decisions>

<canonical_refs>
## Canonical References

**Downstream agents MUST read these before planning or implementing.**

### Project Context
- `.planning/PROJECT.md` — 项目目标和约束
- `.planning/REQUIREMENTS.md` — 需求定义
- `.planning/ROADMAP.md` — Phase 02 详情
- `.planning/research/PITFALLS.md` — 安全问题分析
- `.planning/codebase/CONCERNS.md` — 已识别的安全隐患

### Key Files to Modify
- `hadoken-common/src/main/java/com/github/hadoken/common/util/EncryptUtils.java` — DES 加密，硬编码密钥
- `hadoken-common/src/main/java/com/github/hadoken/common/util/RsaUtils.java` — 1024位RSA密钥

</canonical_refs>

<code_context>
## Existing Code Insights

### Known Security Issues
- EncryptUtils.java: 硬编码密钥 "Passw0rd"、"rhy"
- RsaUtils.java: 1024位弱RSA密钥
- 多处默认密码 "123456"

### Established Patterns
- 配置属性模式：`hadoken.*` 前缀
- 错误处理：GlobalExceptionHandler + HadokenServiceException
- 工具类风格：静态方法，`*Utils` 后缀

</code_context>

<specifics>
## Specific Ideas

用户明确要求：
- 激进升级策略，修复所有安全问题
- 密钥配置化，不硬编码
- 使用现代加密标准（AES-256, RSA-2048）

</specifics>

<deferred>
## Deferred Ideas

None — discussion stayed within phase scope.

</deferred>

---

*Phase: 02-Security Fixes*
*Context gathered: 2026-05-29 via auto-mode*