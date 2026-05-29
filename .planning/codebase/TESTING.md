# Testing Patterns

**Analysis Date:** 2026-05-29

## Test Framework

**Runner:**
- Spring Boot Test 3.3.13 (managed via BOM)
- JUnit 4.12 (defined in dependencies)
- Maven Surefire implicit (no explicit config)

**Assertion Library:**
- Spring Boot Test assertions
- Standard JUnit assertions

**Run Commands:**
```bash
mvn test                    # Run tests (currently skipped)
mvn test -Dmaven.test.skip=false  # Force run tests
mvn verify                  # Run integration tests
```

**Note:** Tests are disabled by default via `<maven.test.skip>true</maven.test.skip>` in `pom.xml`

## Test File Organization

**Location:**
- Not implemented - no `src/test/java` directories exist
- Test module `hadoken-test` serves as integration demo, not unit tests

**Naming:**
- Expected pattern: `*Test.java` or `*Tests.java`
- Currently only demo/test application classes exist (e.g., `ScheduleTest.java` is a demo component)

**Expected Structure:**
```
hadoken-xxx-spring-boot-starter/
├── src/
│   ├── main/java/
│   │   └── com/github/hadoken/framework/xxx/
│   └── test/java/           # Not present
│       └── com/github/hadoken/framework/xxx/
│           ├── XxxTest.java
│           └── XxxIntegrationTest.java
```

## Test Dependencies

**Declared in `hadoken-dependencies/pom.xml`:**
```xml
<!-- Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <version>${spring-boot.version}</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-inline</artifactId>
    <version>5.2.0</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>com.github.fppt</groupId>
    <artifactId>jedis-mock</artifactId>
    <version>1.1.11</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>uk.co.jemos.podam</groupId>
    <artifactId>podam</artifactId>
    <version>8.0.2.RELEASE</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
    <scope>test</scope>
</dependency>
```

**Testing tools:**
- `spring-boot-starter-test` - Integration testing with Spring context
- `mockito-inline` - Mocking static methods and constructors
- `jedis-mock` - In-memory Redis mock for testing
- `podam` - Test data POJO generator
- `junit` - Unit testing framework

## Test Structure

**Expected Suite Organization:**
```java
@SpringBootTest
class XxxIntegrationTest {

    @Autowired
    private XxxService xxxService;

    @Test
    void testSomeMethod() {
        // Test implementation
    }
}
```

**Patterns (expected based on dependencies):**
- Spring Boot integration tests with `@SpringBootTest`
- Mockito for mocking dependencies
- Podam for generating test data objects
- Jedis-mock for Redis-dependent tests

## Mocking

**Framework:** Mockito 5.2.0 (inline version for static mocking)

**Expected Patterns:**
```java
// Standard mocking
@Mock
private TaskStore taskStore;

@InjectMocks
private TaskManagerImpl taskManager;

// Static mocking (mockito-inline)
try (MockedStatic<RedisUtils> mocked = mockStatic(RedisUtils.class)) {
    mocked.when(() -> RedisUtils.getString("key")).thenReturn("value");
    // Test code
}
```

**What to Mock:**
- External services and dependencies
- Repository/data access layers
- Redis operations (via jedis-mock or mockito)
- Time-dependent operations

**What NOT to Mock:**
- Domain entities and value objects
- Pure utility methods with predictable behavior
- Configuration properties

## Fixtures and Factories

**Test Data:**
- Podam for automatic POJO generation:
```java
PodamFactory factory = new PodamFactoryImpl();
TaskDefinition definition = factory.manufacturePojo(TaskDefinition.class);
```

**Location:**
- Not implemented - no fixture files exist
- Expected: `src/test/resources/fixtures/` or inline in test classes

## Coverage

**Requirements:** None enforced

**Coverage Configuration:**
- No explicit JaCoCo or coverage plugin configuration
- Maven test skip enabled by default

**Expected Setup:**
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Test Types

**Unit Tests:**
- Not currently implemented
- Expected: isolated component testing with mocked dependencies
- Use Mockito for mocking
- Use Podam for test data

**Integration Tests:**
- `hadoken-test` module serves as integration demo
- `HadokenTestApplication.java` - Spring Boot application for manual testing
- Configuration in `application.yml` connects to real services

**E2E Tests:**
- Not used

## Common Patterns

**Async Testing:**
```java
@Test
void testAsyncOperation() throws Exception {
    CompletableFuture<Void> future = service.doSomethingAsync();
    future.get(5, TimeUnit.SECONDS); // Wait with timeout
}
```

**Exception Testing:**
```java
@Test
void testExceptionThrown() {
    assertThrows(HadokenServiceException.class, () -> {
        service.throwingMethod();
    });
}

@Test
void testExceptionCode() {
    HadokenServiceException ex = assertThrows(HadokenServiceException.class, () -> {
        HadokenServiceExceptionUtil.exception(ErrorCode.BAD_REQUEST);
    });
    assertEquals(400, ex.getCode());
}
```

**Spring Context Testing:**
```java
@SpringBootTest
@TestPropertySource(locations = "classpath:test-application.yml")
class ServiceIntegrationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context).isNotNull();
    }
}
```

**Redis Mock Testing:**
```java
// Using jedis-mock for Redis-dependent tests
@Test
void testRedisOperation() {
    // jedis-mock provides in-memory Redis
    RedisUtils redisUtils = new RedisUtils(mockRedisTemplate, mockStringRedisTemplate);
    redisUtils.setString("test-key", "test-value");
    assertEquals("test-value", redisUtils.getString("test-key"));
}
```

## Test Module

**hadoken-test module:**
- Purpose: Integration demo and manual testing
- Contains: `HadokenTestApplication.java` as entry point
- Configuration: Real service connections in `application.yml`
- Example components: `ScheduleTest.java` for scheduler testing

**Files:**
- `hadoken-test/src/main/java/com/github/hadoken/framework/test/HadokenTestApplication.java`
- `hadoken-test/src/main/java/com/github/hadoken/framework/test/ScheduleTest.java`
- `hadoken-test/src/main/resources/application.yml`

## Recommendations

**Missing Test Coverage:**
- All modules lack unit tests
- Consider adding `src/test/java` directories
- Consider enabling tests by removing `maven.test.skip=true`

**Test Priority Areas:**
- `hadoken-common` utilities (high reuse, critical)
- `hadoken-redis` RedisUtils (external dependency)
- `hadoken-scheduler` TaskManagerImpl (complex logic)
- `hadoken-web` GlobalExceptionHandler (error handling)

**Test Infrastructure Additions:**
- Add JaCoCo for coverage reporting
- Add test profiles for different environments
- Create test fixtures for common entities

---

*Testing analysis: 2026-05-29*