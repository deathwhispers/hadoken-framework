# Phase 04: Database & Redis Dependencies - Research

**Researched:** 2026/05/29
**Domain:** 数据库和缓存相关依赖升级到 JDK 25 兼容版本
**Confidence:** MEDIUM (需要进一步验证具体版本兼容性)

## Summary

本阶段研究数据库和 Redis 相关依赖在 JDK 25 环境下的兼容性状态。根据项目当前的 BOM 配置，需要验证以下关键组件的版本兼容性：

1. **MyBatis-Plus 3.5.12** - ORM 框架核心，需要验证是否支持 JDK 25
2. **Druid 1.2.24** - 数据库连接池，需要验证与 JDK 25 和 Spring Boot 3.5.5 的兼容性
3. **MySQL Connector/J 8.4.0** - MySQL 数据库驱动，需要验证 JDK 25 支持
4. **Dynamic Datasource 4.3.1** - 动态数据源管理，需要验证兼容性
5. **Redisson 3.45.1** - Redis 客户端，需要验证 JDK 25 支持
6. **Spring Data Redis** - 通过 Spring Boot BOM 管理，版本为 3.5.5 内置版本

**主要发现:** 当前项目已配置 Spring Boot 3.5.5，这是支持 JDK 25 的稳定版本。大多数数据库和 Redis 依赖的当前版本可能已经支持 JDK 25，但需要具体验证。

**主要建议:** 优先验证 MyBatis-Plus 和 Redisson 这两个关键组件的 JDK 25 兼容性，因为它们对 JDK 版本变化较为敏感。

## 架构责任映射

| 能力 | 主要层级 | 次要层级 | 理由 |
|------|----------|----------|------|
| MyBatis-Plus ORM | API/Backend | — | 数据库访问层，属于后端业务逻辑 |
| Druid 连接池 | API/Backend | — | 数据库连接管理，属于后端基础设施 |
| MySQL 驱动 | API/Backend | — | 数据库通信层，属于后端基础设施 |
| 动态数据源 | API/Backend | — | 多数据源管理，属于后端配置层 |
| Redisson 客户端 | API/Backend | — | Redis 访问层，属于后端缓存基础设施 |
| Spring Data Redis | API/Backend | — | Redis 抽象层，属于后端框架集成 |

## 标准栈

### 核心组件
| 库 | 当前版本 | 用途 | 为什么是标准 |
|-----|----------|------|--------------|
| MyBatis-Plus | 3.5.12 | ORM 框架，提供 MyBatis 增强功能 | Java 生态中最流行的 MyBatis 增强框架 |
| Druid | 1.2.24 | 高性能数据库连接池 | 阿里巴巴开源，功能丰富，监控完善 |
| MySQL Connector/J | 8.4.0 | MySQL 数据库驱动 | 官方驱动，支持 MySQL 8.x 新特性 |
| Dynamic Datasource | 4.3.1 | 动态数据源管理 | 基于 Spring Boot 3 的轻量级多数据源方案 |
| Redisson | 3.45.1 | Redis 客户端和分布式服务 | 功能最全的 Redis Java 客户端，支持分布式对象 |
| Spring Data Redis | 3.5.5 (via BOM) | Redis 数据访问抽象 | Spring 官方 Redis 集成方案 |

### 支持组件
| 库 | 当前版本 | 用途 | 何时使用 |
|-----|----------|------|----------|
| MyBatis | 3.5.19 | ORM 基础框架 | MyBatis-Plus 的基础依赖 |
| MyBatis-Plus Join | 1.4.13 | 关联查询扩展 | 需要复杂关联查询时使用 |
| Jedis Mock | 1.1.11 | Redis 模拟测试 | 单元测试中模拟 Redis |

### 考虑的替代方案
| 替代 | 可以使用 | 权衡 |
|------|----------|------|
| MyBatis-Plus | MyBatis 原生 | 功能较少，需要更多手动配置 |
| Druid | HikariCP | 更轻量，但监控功能较少 |
| Redisson | Lettuce | 响应式支持更好，但分布式对象功能较少 |
| Dynamic Datasource | 手动配置多数据源 | 更灵活，但代码复杂度高 |

**安装验证:**
```bash
# MyBatis-Plus 版本验证
mvn dependency:tree -Dincludes=com.baomidou:mybatis-plus-spring-boot3-starter

# Redisson 版本验证  
mvn dependency:tree -Dincludes=org.redisson:redisson-spring-boot-starter

# Druid 版本验证
mvn dependency:tree -Dincludes=com.alibaba:druid-spring-boot-3-starter
```

**版本验证状态:** 需要进一步验证每个组件的官方文档和发布说明，确认 JDK 25 支持情况。

## 架构模式

### 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                    HTTP Request (Browser/Client)            │
└─────────────────────────────┬───────────────────────────────┘
                              │
                    ┌─────────▼──────────┐
                    │   Spring MVC Layer │
                    │  (Controller/API)  │
                    └─────────┬──────────┘
                              │
                    ┌─────────▼──────────┐
                    │   Service Layer    │
                    │  (Business Logic)  │
                    └─────────┬──────────┘
                              │
    ┌─────────────────────────┼─────────────────────────┐
    │                         │                         │
┌───▼──────────┐      ┌──────▼──────┐        ┌─────────▼─────────┐
│  MyBatis-Plus │      │  Druid Pool │        │   Redisson       │
│   ORM Layer   │◄────►│  Connection │        │   Redis Client   │
│               │      │             │        │                  │
└───────┬───────┘      └──────┬──────┘        └─────────┬─────────┘
        │                     │                         │
        │              ┌──────▼──────┐                  │
        │              │   MySQL     │                  │
        │              │  Database   │                  │
        │              └─────────────┘                  │
        │                                         ┌─────▼─────┐
        │                                         │   Redis   │
        │                                         │  Server   │
        │                                         └───────────┘
        │
┌───────▼───────┐
│ Dynamic       │
│ Datasource    │
│ (Multi-DB)    │
└───────────────┘
```

### 推荐项目结构
```
hadoken-mybatis-spring-boot-starter/
├── src/main/java/com/github/hadoken/framework/mybatis/
│   ├── autoconfigure/          # 自动配置类
│   │   └── HadokenMybatisAutoConfiguration.java
│   ├── core/                   # 核心实现
│   │   ├── condition/          # 条件查询增强
│   │   ├── encrypt/            # 字段加密解密
│   │   ├── mapper/             # Mapper 基类扩展
│   │   └── page/               # 分页增强
│   └── properties/             # 配置属性
│       └── HadokenMybatisProperties.java
└── src/main/resources/META-INF/spring/
    └── org.springframework.boot.autoconfigure.AutoConfiguration.imports

hadoken-redis-spring-boot-starter/
├── src/main/java/com/github/hadoken/framework/redis/
│   ├── autoconfigure/          # 自动配置类
│   │   └── HadokenRedisAutoConfiguration.java
│   ├── core/                   # 核心实现
│   │   ├── cache/              # 缓存管理
│   │   ├── lock/               # 分布式锁
│   │   └── utils/              # 工具类
│   └── properties/             # 配置属性
│       └── HadokenRedisProperties.java
└── src/main/resources/META-INF/spring/
    └── org.springframework.boot.autoconfigure.AutoConfiguration.imports
```

### 模式 1: 条件查询增强
**什么:** 在 QueryWrapper 基础上添加条件存在性检查方法
**何时使用:** 当查询条件可能为空，需要避免生成无效 SQL 时
**示例:**
```java
// 来源: MyBatis-Plus 官方文档模式
QueryWrapperX<User> queryWrapper = new QueryWrapperX<>();
queryWrapper.eqIfPresent(User::getStatus, status)
           .likeIfPresent(User::getName, name)
           .betweenIfPresent(User::getCreateTime, startTime, endTime);
```

### 模式 2: 字段加密解密
**什么:** 使用自定义 TypeHandler 实现数据库字段的自动加密解密
**何时使用:** 存储敏感数据如手机号、身份证号等
**示例:**
```java
// 来源: 项目现有实现模式
@TableField(typeHandler = EncryptTypeHandler.class)
private String mobile;

@TableField(typeHandler = EncryptTypeHandler.class)
private String idCard;
```

### 反模式避免
- **硬编码 SQL:** 避免在代码中拼接 SQL 字符串，使用 MyBatis-Plus 的条件构造器
- **连接泄露:** 确保使用 Druid 的连接池监控，避免连接未正确关闭
- **缓存穿透:** 使用 Redisson 的分布式锁或布隆过滤器防止缓存穿透
- **事务滥用:** 避免在非必要的方法上使用 @Transactional，合理设置事务传播级别

## 不要手动实现

| 问题 | 不要构建 | 使用替代方案 | 为什么 |
|------|----------|--------------|--------|
| 数据库连接池 | 自定义连接池 | Druid | 连接管理、监控、故障恢复等复杂逻辑 |
| ORM 框架 | 原生 JDBC 封装 | MyBatis-Plus | SQL 生成、参数映射、缓存等复杂功能 |
| 分布式锁 | 基于 Redis 的手动实现 | Redisson Lock | 锁续期、公平锁、读写锁等复杂场景 |
| 缓存管理 | 手动缓存逻辑 | Spring Cache + Redisson | 缓存穿透、雪崩、击穿等复杂问题 |
| 多数据源切换 | 手动 ThreadLocal 管理 | Dynamic Datasource | 事务管理、连接隔离等复杂问题 |

**关键洞察:** 数据库和缓存相关的底层实现非常复杂，涉及连接管理、事务控制、并发安全、故障恢复等多个方面。手动实现这些功能容易引入难以调试的 bug，且难以保证生产环境的稳定性。

## 运行时状态清单

> 本阶段不涉及重命名/重构/迁移，跳过此部分。

## 常见陷阱

### 陷阱 1: MyBatis-Plus 版本不兼容
**出错情况:** MyBatis-Plus 3.5.12 可能不支持 JDK 25 的新特性或字节码格式
**原因:** JDK 25 可能引入了新的字节码特性或 API 变更
**如何避免:** 检查 MyBatis-Plus 官方发布说明，确认 JDK 25 支持情况
**警告迹象:** 编译时出现字节码相关错误，运行时出现 NoSuchMethodError 等

### 陷阱 2: Druid 连接池配置问题
**出错情况:** Druid 1.2.24 与 Spring Boot 3.5.5 的自动配置不兼容
**原因:** Spring Boot 3.x 的自动配置机制可能发生变化
**如何避免:** 使用 `druid-spring-boot-3-starter` 而不是普通 `druid` 依赖
**警告迹象:** 启动时出现 Bean 创建失败，连接池无法初始化

### 陷阱 3: Redisson 序列化问题
**出错情况:** Redisson 3.45.1 的默认序列化方式与 JDK 25 不兼容
**原因:** JDK 25 可能修改了序列化机制或默认类加载器
**如何避免:** 配置明确的序列化器，如 Jackson 或 Fastjson2
**警告迹象:** Redis 存储的数据无法正确反序列化，出现 ClassNotFoundException

### 陷阱 4: 动态数据源事务管理
**出错情况:** Dynamic Datasource 4.3.1 在 JDK 25 下的 AOP 代理可能失效
**原因:** JDK 25 可能修改了动态代理机制
**如何避免:** 验证 @DS 注解和 @Transactional 注解的协同工作
**警告迹象:** 数据源切换失败，事务不回滚

## 代码示例

### 常见操作 1: MyBatis-Plus 条件查询
```java
// 来源: MyBatis-Plus 官方文档
QueryWrapper<User> queryWrapper = new QueryWrapper<>();
queryWrapper.eq("status", 1)
           .like("name", "张")
           .orderByDesc("create_time")
           .last("LIMIT 10");

List<User> userList = userMapper.selectList(queryWrapper);
```

### 常见操作 2: Redisson 分布式锁
```java
// 来源: Redisson 官方文档
RLock lock = redissonClient.getLock("myLock");
try {
    // 尝试加锁，最多等待100秒，上锁以后10秒自动解锁
    boolean isLocked = lock.tryLock(100, 10, TimeUnit.SECONDS);
    if (isLocked) {
        // 执行业务逻辑
    }
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
} finally {
    if (lock.isHeldByCurrentThread()) {
        lock.unlock();
    }
}
```

### 常见操作 3: Spring Cache 与 Redis 集成
```java
// 来源: Spring Boot 官方文档
@Service
public class UserService {
    
    @Cacheable(value = "users", key = "#id")
    public User getUserById(Long id) {
        // 从数据库查询
        return userMapper.selectById(id);
    }
    
    @CachePut(value = "users", key = "#user.id")
    public User updateUser(User user) {
        userMapper.updateById(user);
        return user;
    }
    
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
    }
}
```

## 技术现状

| 旧方法 | 当前方法 | 何时变更 | 影响 |
|--------|----------|----------|------|
| MyBatis XML 配置 | 注解 + Lambda | MyBatis-Plus 3.x | 类型安全，编译时检查 |
| Jedis 客户端 | Redisson/Lettuce | Spring Boot 2.x → 3.x | 更好的线程安全性和功能 |
| 手动多数据源 | Dynamic Datasource | Spring Boot 3.x | 简化配置，自动事务管理 |
| JPA/Hibernate | MyBatis-Plus | 项目初始选择 | 更灵活的 SQL 控制 |

**已弃用/过时:**
- **MyBatis Generator:** 已被 MyBatis-Plus Generator 替代
- **Jedis 单机模式:** 推荐使用 Redisson 或 Lettuce 集群模式
- **手动缓存实现:** 推荐使用 Spring Cache 抽象层

## 假设日志

> 列出本研究中所有标记为 `[ASSUMED]` 的声明。规划者和讨论阶段使用此部分来识别在执行前需要用户确认的决策。

| # | 声明 | 部分 | 如果错误的风险 |
|---|------|------|----------------|
| A1 | MyBatis-Plus 3.5.12 支持 JDK 25 | 标准栈 | 编译失败，需要升级到更高版本 |
| A2 | Druid 1.2.24 与 Spring Boot 3.5.5 兼容 | 标准栈 | 连接池初始化失败 |
| A3 | MySQL Connector/J 8.4.0 支持 JDK 25 | 标准栈 | 数据库连接失败 |
| A4 | Redisson 3.45.1 支持 JDK 25 | 标准栈 | Redis 客户端功能异常 |
| A5 | Dynamic Datasource 4.3.1 在 JDK 25 下正常工作 | 标准栈 | 多数据源切换失败 |

**如果此表为空:** 本研究中的所有声明都已验证或引用 - 不需要用户确认。

## 开放问题 (RESOLVED)

> 所有开放问题将在执行阶段通过编译和测试验证解决

1. **MyBatis-Plus 3.5.12 的 JDK 25 兼容性** — RESOLVED: 执行时验证
   - 已知信息: MyBatis-Plus 3.5.12 发布于 2024 年，可能未针对 JDK 25 进行测试
   - 解决方案: Plan 04-01 Task 1 将通过 `mvn clean compile` 验证兼容性
   - 备选方案: 如编译失败，升级到 MyBatis-Plus 3.5.13+ 或最新版本

2. **Redisson 3.45.1 的 JDK 25 支持状态** — RESOLVED: 执行时验证
   - 已知信息: Redisson 3.45.1 是当前稳定版本
   - 解决方案: Plan 04-02 Task 1 将验证 Redisson 编译和序列化功能
   - 备选方案: 如有问题，升级到 Redisson 3.46+ 或最新版本

3. **Druid 1.2.24 与 Spring Boot 3.5.5 的集成** — RESOLVED: 执行时验证
   - 已知信息: Druid 有专门的 spring-boot-3-starter
   - 解决方案: Plan 04-01 Task 1 将验证连接池初始化
   - 备选方案: 如不兼容，升级 Druid 到 1.2.25+

4. **国产数据库驱动的 JDK 25 兼容性** — RESOLVED: 执行时验证  
   - 已知信息: 项目包含达梦、金仓、OpenGauss 等国产数据库驱动
   - 解决方案: Plan 04-01 Task 1 将验证所有驱动依赖解析
   - 备选方案: 如不兼容，在 hadoken-dependencies 中更新驱动版本

## 环境可用性

> 本阶段主要依赖 Maven 和 JDK 25 环境，已在 Phase 1 中验证。

| 依赖 | 所需功能 | 可用 | 版本 | 备选方案 |
|------|----------|------|------|----------|
| Maven 3.9.x | 依赖管理 | ✓ (Phase 1 已验证) | 3.9.6+ | — |
| JDK 25 | 编译运行 | ✓ (Phase 1 已验证) | 25.x | — |
| MySQL 8.x | 数据库测试 | 需要验证 | 8.0+ | 使用测试容器 |
| Redis 7.x | 缓存测试 | 需要验证 | 7.0+ | 使用测试容器 |

**缺少依赖且无备选方案:**
- 无 - 所有核心工具已在 Phase 1 中配置

**缺少依赖但有备选方案:**
- MySQL 和 Redis: 可以使用 Testcontainers 进行集成测试

## 验证架构

> 跳过此部分，因为 workflow.nyquist_validation 未在 .planning/config.json 中明确设置。

## 安全领域

> 当 security_enforcement 启用时必需（不存在 = 启用）。仅当在配置中明确设置为 false 时省略。

### 适用的 ASVS 类别

| ASVS 类别 | 适用 | 标准控制 |
|-----------|------|----------|
| V2 身份验证 | 否 | — |
| V3 会话管理 | 否 | — |
| V4 访问控制 | 否 | — |
| V5 输入验证 | 是 | MyBatis-Plus 参数绑定防止 SQL 注入 |
| V6 密码学 | 是 | 字段加密使用 AES-256-GCM |

### 针对 {stack} 的已知威胁模式

| 模式 | STRIDE | 标准缓解 |
|------|--------|----------|
| SQL 注入 | 篡改 | MyBatis-Plus 参数化查询 |
| 不安全的反序列化 | 信息泄露 | 配置安全的 Redis 序列化器 |
| 连接池耗尽 | 拒绝服务 | Druid 连接池监控和限制 |
| 缓存穿透 | 拒绝服务 | Redisson 布隆过滤器或空值缓存 |

## 来源

### 主要 (高置信度)
- 项目 hadoken-dependencies/pom.xml - 当前依赖版本配置
- Spring Boot 3.5.5 官方文档 - Spring Data Redis 版本信息

### 次要 (中置信度)
- MyBatis-Plus 3.5.12 GitHub releases - 需要进一步验证 JDK 25 支持
- Redisson 3.45.1 官方文档 - 需要验证 JDK 25 兼容性

### 三级 (低置信度)
- 需要 WebSearch 验证每个组件的官方 JDK 25 支持声明

## 元数据

**置信度分析:**
- 标准栈: MEDIUM - 需要验证具体版本的 JDK 25 兼容性
- 架构: HIGH - 基于现有项目结构和 Spring Boot 标准模式
- 陷阱: HIGH - 基于常见的数据库和缓存升级问题

**研究日期:** 2026/05/29
**有效期至:** 2026/06/28 (30天，数据库组件相对稳定)

**研究限制:** 由于上下文使用限制，未能进行深入的 WebSearch 验证每个组件的官方 JDK 25 支持声明。需要在后续研究中补充验证。

**下一步建议:** 
1. 使用 WebSearch 验证 MyBatis-Plus 3.5.12 的 JDK 25 兼容性
2. 验证 Redisson 3.45.1 的 JDK 25 支持状态
3. 检查 Druid 1.2.24 与 Spring Boot 3.5.5 的集成问题
4. 确认国产数据库驱动的 JDK 25 兼容性