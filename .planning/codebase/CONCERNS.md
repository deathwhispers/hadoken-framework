# Codebase Concerns

**Analysis Date:** 2026-05-29

## Tech Debt

**ValidationUtil incomplete implementations:**
- Issue: `isMobile()` only checks length, no actual format validation (TODO comment)
- Issue: `isEmail()` always returns `true` - stub implementation
- Files: `hadoken-common/src/main/java/com/github/hadoken/common/util/validation/ValidationUtil.java`
- Impact: Invalid user data can pass validation, potential security/data quality issues
- Fix approach: Implement proper regex validation for mobile and email formats

**AbstractStreamMessageListener incomplete features:**
- Issue: TODO comments indicating unhandled concerns: exception handling, logging/transaction integration, idempotency, retry mechanism
- Files: `hadoken-mq-spring-boot-starter/src/main/java/com/github/hadoken/framework/mq/core/stream/AbstractStreamMessageListener.java:75`
- Impact: Message processing failures may not be handled properly, potential data loss
- Fix approach: Implement robust exception handling, idempotency checks, and retry logic

**Commented-out code in GlobalExceptionHandler:**
- Issue: Lines 276-283 contain commented-out code for request params, method, user agent, IP, and time
- Files: `hadoken-web-spring-boot-starter/src/main/java/com/github/hadoken/framework/web/mvc/core/handler/GlobalExceptionHandler.java`
- Impact: Missing important audit information in error logs
- Fix approach: Uncomment and complete the implementation or remove permanently

## Known Bugs

**Email validation stub:**
- Symptoms: All email strings pass validation regardless of format
- Files: `hadoken-common/src/main/java/com/github/hadoken/common/util/validation/ValidationUtil.java:46`
- Trigger: Call `isEmail()` with any string
- Workaround: None - use external validation library or implement proper regex

**Mobile validation incomplete:**
- Symptoms: Only checks for 11-character length, accepts invalid formats like "00000000000"
- Files: `hadoken-common/src/main/java/com/github/hadoken/common/util/validation/ValidationUtil.java:25`
- Trigger: Call `isMobile()` with 11-character non-phone string
- Workaround: Implement additional format validation

## Security Considerations

**Hardcoded encryption keys:**
- Risk: Critical - encryption keys hardcoded in source code
- Files: 
  - `hadoken-common/src/main/java/com/github/hadoken/common/util/EncryptUtils.java:34-45` - "Passw0rd"
  - `hadoken-mybatis-spring-boot-starter/src/main/java/com/github/hadoken/framework/mybatis/core/util/DBAESUtil.java:16-18` - KEY="rhy", IV="6859505890402435"
- Current mitigation: None
- Recommendations: 
  - Move all encryption keys to environment variables or secure key management
  - Use proper key derivation functions
  - Never hardcode secrets in source code

**Weak RSA key size:**
- Risk: RSA 1024-bit keys are considered weak by modern standards
- Files: `hadoken-common/src/main/java/com/github/hadoken/common/util/RsaUtils.java:151`
- Current mitigation: None
- Recommendations: Upgrade to at least 2048-bit RSA keys

**Weak default mock secret:**
- Risk: Default password "123456" in configuration
- Files: `hadoken-security-spring-boot-starter/src/main/java/com/github/hadoken/framework/security/autoconfigure/SecurityProperties.java:53`
- Current mitigation: Validation requires non-empty value
- Recommendations: Remove default value, require explicit configuration

**DES encryption usage:**
- Risk: DES algorithm is deprecated and considered weak
- Files: `hadoken-common/src/main/java/com/github/hadoken/common/util/EncryptUtils.java`
- Current mitigation: None
- Recommendations: Replace with AES-256 or stronger algorithm

**Token exposure in WebSocket:**
- Risk: Authorization token logged in WebSocket interceptor
- Files: `hadoken-websocket-spring-boot-starter/src/main/java/com/github/hadoken/framework/websocket/core/interceptor/HadokenWebSocketHandleInterceptor.java:37`
- Current mitigation: INFO level logging
- Recommendations: Never log authentication tokens, use DEBUG level or omit

## Performance Bottlenecks

**RedisUtils large file:**
- Problem: Single file with 919 lines containing all Redis operations
- Files: `hadoken-redis-spring-boot-starter/src/main/java/com/github/hadoken/framework/redis/util/RedisUtils.java`
- Cause: Monolithic utility class without modularization
- Improvement path: Split into focused classes (StringOps, HashOps, ListOps, SetOps)

**Redis SCAN pagination:**
- Problem: `findKeysForPage()` scans all keys first, then paginates in memory
- Files: `hadoken-redis-spring-boot-starter/src/main/java/com/github/hadoken/framework/redis/util/RedisUtils.java:114`
- Cause: Redis SCAN doesn't support direct pagination
- Improvement path: Document limitation clearly, add warning for large key sets

**TaskManagerImpl complexity:**
- Problem: 402 lines in single class managing task lifecycle
- Files: `hadoken-scheduler-spring-boot-starter/src/main/java/com/github/hadoken/framework/scheduler/manager/TaskManagerImpl.java`
- Cause: Multiple responsibilities combined (registration, scheduling, state management)
- Improvement path: Consider splitting into separate managers

## Fragile Areas

**ByteArrHelper conflicting patterns:**
- Files: `hadoken-common/src/main/java/com/github/hadoken/common/util/ByteArrHelper.java`
- Why fragile: Uses both singleton pattern (`getInstance()`) and Spring `@Component` annotation - conflicting DI patterns
- Safe modification: Remove singleton pattern, rely solely on Spring DI
- Test coverage: No unit tests

**Utility classes with main() methods:**
- Files:
  - `hadoken-common/src/main/java/com/github/hadoken/common/util/ByteArrHelper.java:386`
  - `hadoken-common/src/main/java/com/github/hadoken/common/util/RsaUtils.java:22`
  - `hadoken-common/src/main/java/com/github/hadoken/common/util/HexUtils.java:258`
- Why fragile: main() methods used for ad-hoc testing instead of proper unit tests
- Safe modification: Convert to proper JUnit tests
- Test coverage: None for these utility methods

**DefaultEncryptor/DefaultDecryptor error handling:**
- Files:
  - `hadoken-mybatis-spring-boot-starter/src/main/java/com/github/hadoken/framework/mybatis/core/codec/DefaultEncryptor.java:38`
  - `hadoken-mybatis-spring-boot-starter/src/main/java/com/github/hadoken/framework/mybatis/core/codec/DefaultDecryptor.java:42`
- Why fragile: Uses `e.printStackTrace()` instead of proper logging
- Safe modification: Replace with `log.error()` calls
- Test coverage: No unit tests for encryption/decryption failure scenarios

## Scaling Limits

**Redis connection management:**
- Current capacity: Single RedisUtils instance per application
- Limit: May not handle multiple Redis clusters or sharding
- Scaling path: Support multiple RedisTemplate configurations

**Scheduler distributed locking:**
- Current capacity: Optional DistributedLockProvider
- Limit: Lock acquisition timeout and retry not configurable
- Scaling path: Add configurable lock timeout and retry parameters

## Dependencies at Risk

**DES encryption:**
- Risk: Deprecated algorithm, may be removed in future Java versions
- Impact: EncryptUtils will fail
- Migration plan: Replace with AES implementation

**Tomcat codec dependency:**
- Risk: RsaUtils uses `org.apache.tomcat.util.codec.binary.Base64`
- Files: `hadoken-common/src/main/java/com/github/hadoken/common/util/RsaUtils.java:3`
- Impact: Coupled to Tomcat runtime
- Migration plan: Use `java.util.Base64` (standard Java 8+ API)

## Missing Critical Features

**Unit test coverage:**
- Problem: No unit test files found in entire codebase
- Blocks: Cannot verify correctness of utility methods, handlers, or core logic
- Impact: High risk of undetected bugs during changes

**Idempotency in message processing:**
- Problem: Redis Stream message processing lacks idempotency checks
- Blocks: Duplicate message processing can cause data corruption
- Impact: Critical for production reliability

**Message retry mechanism:**
- Problem: No retry logic for failed message consumption
- Blocks: Transient failures cause permanent message loss
- Impact: Data integrity issues

## Test Coverage Gaps

**No unit tests anywhere:**
- What's not tested: All utility classes, handlers, interceptors, mappers, services
- Files: Only test application files exist (`hadoken-test/src/main/java/...`)
- Risk: Any change can introduce bugs without detection
- Priority: High - critical for framework reliability

**Encryption/decryption not tested:**
- What's not tested: EncryptUtils, DBAESUtil, RsaUtils functionality
- Files:
  - `hadoken-common/src/main/java/com/github/hadoken/common/util/EncryptUtils.java`
  - `hadoken-mybatis-spring-boot-starter/src/main/java/com/github/hadoken/framework/mybatis/core/util/DBAESUtil.java`
  - `hadoken-common/src/main/java/com/github/hadoken/common/util/RsaUtils.java`
- Risk: Encryption failures may corrupt data silently
- Priority: Critical - security-related code must be tested

**Validation not tested:**
- What's not tested: ValidationUtil methods (isMobile, isEmail)
- Files: `hadoken-common/src/main/java/com/github/hadoken/common/util/validation/ValidationUtil.java`
- Risk: Invalid data accepted, security vulnerabilities
- Priority: High

**Exception handlers not tested:**
- What's not tested: GlobalExceptionHandler behavior for various exception types
- Files: `hadoken-web-spring-boot-starter/src/main/java/com/github/hadoken/framework/web/mvc/core/handler/GlobalExceptionHandler.java`
- Risk: Error responses may be incorrect
- Priority: Medium

---

*Concerns audit: 2026-05-29*