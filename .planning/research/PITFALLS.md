# Domain Pitfalls: JDK 25 + Spring Boot Upgrade

**Domain:** JDK 17 -> JDK 25 + Spring Boot 3.3.13 -> Latest Upgrade
**Researched:** 2026-05-29
**Target:** JDK 25 + Spring Boot 3.5.x (or latest stable supporting JDK 25)

---

## Critical Pitfalls

Mistakes that cause rewrites or major issues. These MUST be addressed before or during upgrade.

### Pitfall 1: DES Encryption Removal Risk

**What goes wrong:** JDK 21+ deprecates DES algorithm, JDK 25 may remove it entirely. Current codebase uses DES in `EncryptUtils.java`.

**Why it happens:** DES is a 56-bit symmetric cipher considered insecure since 1998. Modern JDKs deprecate weak algorithms.

**Consequences:**
- `NoSuchAlgorithmException` at runtime
- Data encrypted with DES becomes unreadable
- Production failures in encryption/decryption operations

**Prevention:**
1. Audit all `javax.crypto.Cipher.getInstance("DES")` calls
2. Migrate to AES-256-GCM before upgrade
3. Create data migration path for existing encrypted data
4. Test decryption of legacy data with new algorithm

**Detection:**
- Search for: `DES`, `DESede`, `Blowfish` in crypto code
- Files affected:
  - `hadoken-common/src/main/java/com/github/hadoken/common/util/EncryptUtils.java`
- Compile with `-Xlint:deprecation` to see warnings

**Phase:** Phase 1 - Pre-Upgrade Audit

---

### Pitfall 2: RSA 1024-bit Key Incompatibility

**What goes wrong:** RSA keys smaller than 2048 bits are disabled by default in newer JDKs. Current codebase uses 1024-bit RSA keys.

**Why it happens:** NIST deprecated 1024-bit RSA in 2013. JDK security defaults tightened progressively.

**Consequences:**
- `InvalidKeyException: Key size too small`
- Digital signature verification failures
- SSL/TLS handshake failures
- Security audit failures

**Prevention:**
1. Generate new 2048-bit or 4096-bit RSA key pairs
2. Update `RsaUtils.java` default key size
3. Re-encrypt all data encrypted with old keys
4. Plan key rotation strategy for production systems

**Detection:**
- Search for: `RSA`, `1024`, `KeyPairGenerator` in crypto code
- File affected: `hadoken-common/src/main/java/com/github/hadoken/common/util/RsaUtils.java:151`
- Run security scanner before upgrade

**Phase:** Phase 1 - Pre-Upgrade Audit

---

### Pitfall 3: Tomcat Base64 Dependency

**What goes wrong:** Code uses `org.apache.tomcat.util.codec.binary.Base64` which is Tomcat-specific and may not be available or compatible in newer runtimes.

**Why it happens:** Historical copy of Apache Commons Codec bundled with Tomcat. Not part of JDK standard API.

**Consequences:**
- ClassNotFoundException when Tomcat internals change
- Incompatibility with other servlet containers (Jetty, Undertow)
- Breaking changes across Tomcat versions

**Prevention:**
1. Replace with `java.util.Base64` (available since JDK 8)
2. No behavioral changes - same encoding results
3. Remove Tomcat-specific dependency

**Detection:**
- Search for: `org.apache.tomcat.util.codec`
- File affected: `hadoken-common/src/main/java/com/github/hadoken/common/util/RsaUtils.java:3`
- Use `grep -r "org.apache.tomcat.util.codec" .`

**Phase:** Phase 1 - Pre-Upgrade Audit

---

### Pitfall 4: Hardcoded Encryption Keys

**What goes wrong:** Encryption keys hardcoded in source code will be exposed in version control and built artifacts.

**Why it happens:** Convenience during development, never migrated to proper secret management.

**Consequences:**
- Security audit failure
- Keys leaked in git history
- Cannot rotate keys without code changes
- Compliance violations (PCI-DSS, SOC2, etc.)

**Prevention:**
1. Move all keys to environment variables
2. Use Spring's `jasypt-spring-boot` for property encryption
3. Integrate with HashiCorp Vault or AWS Secrets Manager
4. Rotate all keys immediately after migration

**Detection:**
- Search for: string literals matching key patterns
- Files affected:
  - `hadoken-common/src/main/java/com/github/hadoken/common/util/EncryptUtils.java:34-45` - "Passw0rd"
  - `hadoken-mybatis-spring-boot-starter/src/main/java/com/github/hadoken/framework/mybatis/core/util/DBAESUtil.java:16-18` - KEY="rhy", IV="6859505890402435"
  - `hadoken-security-spring-boot-starter/src/main/java/com/github/hadoken/framework/security/autoconfigure/SecurityProperties.java:53` - "123456"

**Phase:** Phase 1 - Pre-Upgrade Audit (CRITICAL)

---

### Pitfall 5: No Unit Tests

**What goes wrong:** Without tests, upgrade will silently break functionality. You won't know what's broken until production.

**Why it happens:** Framework development prioritized features over testing.

**Consequences:**
- Unknown behavior changes after upgrade
- No early warning system for API changes
- High risk of production incidents
- Cannot confidently refactor or migrate

**Prevention:**
1. Add unit tests for all utility classes FIRST
2. Add integration tests for critical paths
3. Establish 70%+ coverage before upgrade
4. Run full test suite after each dependency bump

**Detection:**
- Check for `src/test/java` directories
- Run `mvn test` and verify test count > 0
- Current state: Only test application files exist, no unit tests

**Phase:** Phase 1 - Pre-Upgrade Audit (CRITICAL)

---

### Pitfall 6: Spring Boot Security Configuration Changes

**What goes wrong:** Spring Boot 3.4+ changes security configuration patterns. WebSecurityConfigurerAdapter deprecated in favor of SecurityFilterChain.

**Why it happens:** Spring Security 6.x modernized configuration approach.

**Consequences:**
- Application fails to start
- Security rules not applied
- Authentication/authorization failures
- 401/403 errors on previously working endpoints

**Prevention:**
1. Review `HadokenWebSecurityConfigurerAdapter.java`
2. Migrate from `WebSecurityConfigurerAdapter` to `SecurityFilterChain` bean
3. Update `authorizeHttpRequests` instead of `antMatchers`
4. Test all security rules after migration

**Detection:**
- Search for: `WebSecurityConfigurerAdapter`, `antMatchers`, `accessDeniedHandler`
- Files affected:
  - `hadoken-security-spring-boot-starter/src/main/java/com/github/hadoken/framework/security/autoconfigure/HadokenWebSecurityConfigurerAdapter.java`
- Compile with deprecation warnings

**Phase:** Phase 2 - Core Upgrade

---

### Pitfall 7: Dependency Version Conflicts

**What goes wrong:** Spring Boot 3.5+ may require updated dependency versions that conflict with current versions.

**Why it happens:** Spring Boot BOM manages dependency versions, but explicit versions may conflict.

**Consequences:**
- ClassNotFoundException at runtime
- Method signature changes
- NoClassDefFoundError
- Infinite recursion in dependency resolution

**Prevention:**
1. Review all explicit version declarations in `hadoken-dependencies/pom.xml`
2. Prefer Spring Boot BOM-managed versions
3. Check each dependency's compatibility with JDK 25
4. Use `mvn dependency:tree` to find conflicts

**Key Dependencies to Verify:**

| Dependency | Current Version | Concern |
|------------|-----------------|---------|
| MyBatis Plus | 3.5.12 | JDK 25 compatibility |
| Redisson | 3.45.1 | JDK 25, Netty compatibility |
| Druid | 1.2.24 | JDK 25, driver compatibility |
| SkyWalking | 9.0.0 | JDK 25 agent compatibility |
| Hutool | 5.8.39 | JDK 25 API usage |
| Eclipse Paho MQTT | 1.2.5 | JDK 25, Netty compatibility |
| Netty | 4.1.116.Final | JDK 25 compatibility |
| Flowable | 7.1.0 | JDK 25 compatibility |
| 国产数据库驱动 | Various | JDK 25 compatibility |

**Detection:**
- Run `mvn versions:display-dependency-updates`
- Check each library's GitHub issues for JDK 25 support
- Run integration tests after each major dependency update

**Phase:** Phase 2 - Core Upgrade

---

### Pitfall 8:国产数据库驱动兼容性

**What goes wrong:** 国产数据库驱动 (达梦、金仓、OpenGauss、TDengine) 可能不支持 JDK 25。

**Why it happens:** 国产数据库驱动更新频率较低，可能未及时适配最新 JDK。

**Consequences:**
- 无法连接国产数据库
- 驱动类加载失败
- 特定功能不可用
- 客户环境部署失败

**Prevention:**
1. 联系各数据库厂商确认 JDK 25 支持计划
2. 准备降级方案 (JDK 21 备选)
3. 测试所有国产数据库连接
4. 更新驱动版本到最新兼容版本

**Affected Drivers:**
- 达梦 DM8: `dm8.jdbc.version` 8.1.3.140
- 金仓 KingBase: `kingbase.jdbc.version` 8.6.0
- OpenGauss: `opengauss.jdbc.version` 5.1.0
- TDengine: `taos.version` 3.3.3

**Detection:**
- 在 JDK 25 环境下运行数据库连接测试
- 检查驱动厂商发布说明

**Phase:** Phase 2 - Core Upgrade

---

## Moderate Pitfalls

### Pitfall 9: Virtual Thread Compatibility

**What goes wrong:** JDK 21+ introduces virtual threads, but not all libraries support them. Redisson, Netty, and database drivers may have issues.

**Why it happens:** Virtual threads change threading model, pinning can occur with synchronized blocks.

**Consequences:**
- Performance degradation instead of improvement
- Thread pinning warnings
- Deadlocks in synchronized code
- Unexpected behavior in thread-local scenarios

**Prevention:**
1. Review libraries for virtual thread support
2. Replace `synchronized` with `ReentrantLock` in hot paths
3. Test with `-Djdk.tracePinnedThreads=full` to detect pinning
4. Update Netty to 4.1.110+ for virtual thread support

**Detection:**
- Enable virtual thread pinning detection
- Monitor for `VirtualThread` warnings
- Files to check:
  - `hadoken-redis-spring-boot-starter` (Redisson)
  - `hadoken-mybatis-spring-boot-starter` (Druid)
  - `hadoken-web-spring-boot-starter` (Tomcat)

**Phase:** Phase 3 - Post-Upgrade Optimization

---

### Pitfall 10: Structured Concurrency Changes

**What goes wrong:** JDK 25's structured concurrency API may have changed from preview versions.

**Why it happens:** Structured Concurrency went through multiple preview iterations before stabilization.

**Consequences:**
- Compilation failures
- Runtime exceptions
- API signature mismatches

**Prevention:**
1. Don't use preview APIs in production code
2. Wait for final API stabilization
3. Use `ExecutorService` with virtual threads instead

**Detection:**
- Search for `java.util.concurrent.StructuredTaskScope`
- Check `--enable-preview` flags

**Phase:** Phase 3 - Post-Upgrade Optimization

---

### Pitfall 11: ZGC and Memory Management Changes

**What goes wrong:** JDK 25's ZGC improvements may require different JVM tuning parameters.

**Why it happens:** Garbage collectors evolve with each JDK version.

**Consequences:**
- Suboptimal memory usage
- Longer or shorter GC pauses than expected
- Different heap behavior

**Prevention:**
1. Review current GC settings
2. Test with ZGC enabled: `-XX:+UseZGC`
3. Monitor GC logs after upgrade
4. Adjust heap sizing if needed

**Detection:**
- Enable GC logging: `-Xlog:gc*`
- Compare GC behavior before/after upgrade

**Phase:** Phase 3 - Post-Upgrade Optimization

---

### Pitfall 12: Spring Boot Configuration Property Changes

**What goes wrong:** Spring Boot 3.4+ deprecates or removes certain configuration properties.

**Why it happens:** Spring Boot regularly cleans up and renames configuration properties.

**Consequences:**
- Configuration ignored silently
- Startup failures
- Different behavior in production

**Prevention:**
1. Run Spring Boot's configuration property migration
2. Use `spring-boot-properties-migrator` during upgrade
3. Review `application.yml` for deprecated properties
4. Check Spring Boot release notes for property changes

**Common Property Changes:**
- `spring.redis.*` -> `spring.data.redis.*`
- `spring.datasource.*` pool settings renamed
- Actuator endpoint paths changed

**Detection:**
- Add `spring-boot-properties-migrator` dependency temporarily
- Check startup logs for deprecation warnings

**Phase:** Phase 2 - Core Upgrade

---

### Pitfall 13: Validation API Changes

**What goes wrong:** Jakarta Validation API may have changed behavior in newer versions.

**Why it happens:** Jakarta EE 10+ updates validation specifications.

**Consequences:**
- Validation rules not applied
- Different validation error messages
- Custom validators failing

**Prevention:**
1. Review `jakarta.validation` usage
2. Test all validation scenarios
3. Check `SecurityProperties.java` validation annotations

**Detection:**
- Search for `@NotNull`, `@NotEmpty`, `@Valid`
- Files affected:
  - `hadoken-security-spring-boot-starter/src/main/java/com/github/hadoken/framework/security/autoconfigure/SecurityProperties.java`

**Phase:** Phase 2 - Core Upgrade

---

### Pitfall 14: TransmittableThreadLocal Compatibility

**What goes wrong:** `transmittable-thread-local` library may not support virtual threads correctly.

**Why it happens:** TTL designed for platform threads, may behave differently with virtual threads.

**Consequences:**
- Security context lost in async operations
- Trace ID not propagated
- Incorrect user context in background threads

**Prevention:**
1. Update `transmittable-thread-local` to 2.14.5+
2. Test async security context propagation
3. Consider alternatives for virtual thread environments

**Detection:**
- File affected: Security module uses TTL for context propagation
- Test security context in `@Async` methods

**Phase:** Phase 2 - Core Upgrade

---

### Pitfall 15: SkyWalking Agent Compatibility

**What goes wrong:** SkyWalking Java agent may not support JDK 25 bytecode.

**Why it happens:** Java agents use bytecode instrumentation that must support target JDK version.

**Consequences:**
- Application fails to start with agent
- Incomplete tracing data
- Performance degradation

**Prevention:**
1. Update SkyWalking agent to latest version
2. Verify JDK 25 support in SkyWalking release notes
3. Test agent attachment before production deployment
4. Have fallback plan to disable APM temporarily

**Detection:**
- Current version: SkyWalking 9.0.0
- Check Apache SkyWalking GitHub for JDK 25 support
- Files affected:
  - `hadoken-monitor-spring-boot-starter`

**Phase:** Phase 2 - Core Upgrade

---

## Minor Pitfalls

### Pitfall 16: Maven Compiler Plugin Version

**What goes wrong:** Older Maven compiler plugin may not properly handle JDK 25 features.

**Why it happens:** Plugin needs updates for new Java language features.

**Consequences:**
- Compilation failures
- Incorrect bytecode generation
- Unable to use new language features

**Prevention:**
1. Update `maven-compiler-plugin` to 3.13+
2. Set `<release>25</release>` instead of source/target
3. Verify `maven.test.skip` doesn't hide test failures

**Detection:**
- Current version: 3.12.1
- Check for compilation warnings

**Phase:** Phase 2 - Core Upgrade

---

### Pitfall 17: Lombok Compatibility

**What goes wrong:** Lombok may have issues with JDK 25 bytecode manipulation.

**Why it happens:** Lombok uses internal JDK APIs for annotation processing.

**Consequences:**
- Compilation errors with Lombok annotations
- Missing generated methods
- IDE sync issues

**Prevention:**
1. Update Lombok to 1.18.38+ (current version should work)
2. Test all Lombok annotations compile correctly
3. Update IDE Lombok plugin

**Detection:**
- Current version: 1.18.38 (likely compatible)
- Compile with `-Xlint:all` to see warnings

**Phase:** Phase 2 - Core Upgrade

---

### Pitfall 18: MapStruct Compatibility

**What goes wrong:** MapStruct annotation processor may need update for JDK 25.

**Why it happens:** Annotation processors interact with javac internals.

**Consequences:**
- Generated mapper classes fail to compile
- Runtime mapper failures
- Build failures

**Prevention:**
1. Update MapStruct to latest version
2. Verify annotation processor configuration
3. Test all mapper classes

**Detection:**
- Current version: 1.6.3
- Check MapStruct release notes for JDK 25 support

**Phase:** Phase 2 - Core Upgrade

---

### Pitfall 19: Test Framework Compatibility

**What goes wrong:** Mockito inline may have issues with JDK 25's module system.

**Why it happens:** Mockito inline uses internal APIs that may be restricted.

**Consequences:**
- Test failures when mocking
- Unable to mock final classes/methods
- Reflection errors

**Prevention:**
1. Update Mockito to 5.14+ for JDK 25 support
2. Add JVM args: `--add-opens java.base/java.lang=ALL-UNNAMED`
3. Consider using Mockito without inline if issues persist

**Detection:**
- Current version: mockito-inline 5.2.0
- Run unit tests to detect mocking issues

**Phase:** Phase 1 - Pre-Upgrade Audit

---

### Pitfall 20: Docker Base Image Compatibility

**What goes wrong:** Docker images need to use JDK 25 base images.

**Why it happens:** Most Docker images default to older JDK versions.

**Consequences:**
- Wrong JDK version in container
- Missing JDK 25 features
- Security vulnerabilities in older images

**Prevention:**
1. Update Dockerfile base images
2. Use `eclipse-temurin:25-jdk` or similar
3. Verify JDK version in running container

**Detection:**
- Search for `FROM openjdk`, `FROM java`, `FROM eclipse-temurin` in Dockerfiles
- Run `java -version` in container

**Phase:** Phase 2 - Core Upgrade

---

## Phase-Specific Warnings

| Phase Topic | Likely Pitfall | Mitigation |
|-------------|---------------|------------|
| Security Audit | Hardcoded keys, weak crypto | Externalize keys, upgrade algorithms |
| Unit Test Creation | No existing tests | Write tests before upgrade |
| JDK Update | DES removal, RSA key size | Migrate crypto first |
| Dependency Update | Version conflicts, 国产驱动 | Incremental updates, test each |
| Spring Boot Update | Security config changes | Review SecurityFilterChain migration |
| Integration Testing | Driver incompatibilities | Test all databases |
| Performance Testing | Virtual thread pinning | Enable pinning detection |

---

## Quick Reference: Breaking Changes by JDK Version

### JDK 17 -> JDK 21
- Pattern matching for switch (final)
- Record patterns
- Virtual threads (preview -> final)
- Sequenced collections
- `Thread.stop()` removed

### JDK 21 -> JDK 25 (anticipated)
- Further virtual thread optimizations
- Structured concurrency evolution
- Value types (Project Valhalla preview)
- Foreign function & memory API final
- Possible DES removal
- Stricter security defaults

---

## Upgrade Order Recommendation

1. **Phase 1: Security & Testing Foundation**
   - Address hardcoded keys (Pitfall 4)
   - Create unit tests (Pitfall 5)
   - Replace Tomcat Base64 (Pitfall 3)
   - Upgrade DES to AES (Pitfall 1)
   - Upgrade RSA key size (Pitfall 2)

2. **Phase 2: Core Upgrade**
   - Update Spring Boot (Pitfall 6, 12)
   - Update dependencies (Pitfall 7, 8)
   - Update build tools (Pitfall 16, 17, 18)
   - Test database drivers (Pitfall 8)
   - Verify SkyWalking (Pitfall 15)

3. **Phase 3: Optimization**
   - Virtual thread tuning (Pitfall 9)
   - GC optimization (Pitfall 11)
   - Performance testing

---

## Sources

- OpenJDK JEPs and Release Notes (JDK 17, 21, 25)
- Spring Boot 3.4/3.5 Release Notes
- Spring Security 6.x Migration Guide
- Apache SkyWalking Documentation
- MyBatis Plus GitHub Issues
- Redisson Documentation
- 各国产数据库官方文档
- CONCERNS.md analysis (project-specific issues)
- INTEGRATIONS.md analysis (dependency-specific issues)

---

*Research conducted: 2026-05-29*
*Confidence: MEDIUM (web access restricted, based on training data and project analysis)*