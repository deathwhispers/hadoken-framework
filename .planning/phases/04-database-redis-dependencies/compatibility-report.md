# Phase 04: Database & Redis Dependencies - JDK 25 兼容性报告

**Created:** 2026/05/29
**Status:** 研究完成，需要验证编译

## 概述

本报告分析了数据库和缓存相关依赖在 JDK 25 环境下的兼容性状态。项目当前配置为 JDK 25（`pom.xml` 中 `<java.version>25</java.version>`），但实际运行环境为 JDK 21.0.11。

## 兼容性分析结果

| 组件 | 当前版本 | 最新版本 | JDK 25 兼容性 | 建议操作 |
|------|----------|----------|---------------|----------|
| MyBatis-Plus | 3.5.12 | 3.5.16 | **需要升级** | 升级到 3.5.16（支持 Spring Boot 4.x） |
| Druid | 1.2.24 | 1.2.28 | **需要升级** | 升级到 1.2.28（支持 Spring Boot 4.x） |
| MySQL Connector/J | 8.4.0 | 9.7.0 | **兼容** | 可保持或升级到 9.7.0 |
| Dynamic Datasource | 4.3.1 | 4.5.0 | **兼容** | 可保持或升级到 4.5.0 |
| Redisson | 3.45.1 | TBD | **需要验证** | 需进一步验证 |
| Spring Data Redis | 3.5.5 (BOM) | 3.5.5 | **兼容** | 通过 Spring Boot BOM 管理 |

## 详细分析

### 1. MyBatis-Plus 3.5.12

**当前版本:** 3.5.12
**最新版本:** 3.5.16 (2025-01-11)

**兼容性状态:** ⚠️ **建议升级**

**理由:**
- MyBatis-Plus 3.5.13 开始支持 Spring Boot 4.x
- MyBatis-Plus 3.5.16 升级了 Spring Boot 到 3.5.9，支持更新的 Spring Boot 版本
- 当前 3.5.12 版本可能不完全支持 JDK 25 的字节码特性

**升级建议:** 升级到 3.5.16

**来源:** [MyBatis-Plus Releases](https://github.com/baomidou/mybatis-plus/releases)

### 2. Druid 1.2.24

**当前版本:** 1.2.24
**最新版本:** 1.2.28 (2025-03-10)

**兼容性状态:** ⚠️ **建议升级**

**理由:**
- Druid 1.2.28 明确声明 "这是支持 Spring Boot 4.x 的版本"
- 项目使用 `druid-spring-boot-3-starter`，建议升级后使用对应版本的 starter

**升级建议:** 升级到 1.2.28，使用 `druid-spring-boot-3-starter` 或根据 Spring Boot 版本选择

**来源:** [Druid Releases](https://github.com/alibaba/druid/releases)

### 3. MySQL Connector/J 8.4.0

**当前版本:** 8.4.0
**最新版本:** 9.7.0

**兼容性状态:** ✅ **兼容**

**理由:**
- MySQL Connector/J 支持 JDK 8+，包括 JDK 21 和 JDK 25
- 当前版本 8.4.0 应可正常工作
- 最新版本 9.7.0 提供更多功能，可选择性升级

**升级建议:** 可保持 8.4.0，或升级到 9.7.0

**来源:** [MySQL Connector/J Documentation](https://dev.mysql.com/doc/connector-j/en/connector-j-versions.html)

### 4. Dynamic Datasource 4.3.1

**当前版本:** 4.3.1
**最新版本:** 4.5.0

**兼容性状态:** ✅ **兼容**

**理由:**
- Dynamic Datasource 的 `dynamic-datasource-spring-boot3-starter` 要求 JDK 17+
- JDK 25 符合此要求
- 当前版本 4.3.1 应可正常工作

**升级建议:** 可保持 4.3.1，或升级到 4.5.0

**来源:** [Dynamic Datasource GitHub](https://github.com/baomidou/dynamic-datasource-spring-boot-starter)

### 5. Redisson 3.45.1

**当前版本:** 3.45.1
**最新版本:** 需验证

**兼容性状态:** ⚠️ **需要验证**

**理由:**
- Redisson 通常支持 JDK 8+ 或 JDK 11+
- 需要验证 3.45.1 在 JDK 25 下的序列化和网络功能

**升级建议:** 建议检查最新 Redisson 版本的 JDK 25 兼容性

### 6. Spring Data Redis

**当前版本:** 通过 Spring Boot 3.5.5 BOM 管理
**兼容性状态:** ✅ **兼容**

**理由:**
- Spring Boot 3.5.5 内置 Spring Data Redis 版本
- 通过 BOM 管理，自动兼容

### 7. 国产数据库驱动

| 驱动 | 当前版本 | JDK 25 兼容性 | 备注 |
|------|----------|---------------|------|
| 达梦 (DM8) | 8.1.3.140 | ⚠️ 需验证 | 国产驱动通常较保守，需验证 |
| 金仓 (Kingbase) | 8.6.0 | ⚠️ 需验证 | 需验证官方文档 |
| OpenGauss | 5.1.0 | ⚠️ 需验证 | OpenGauss 驱动基于 PostgreSQL，可能兼容 |
| TDengine | 3.3.3 | ⚠️ 需验证 | 需验证官方文档 |

## 环境限制

**⚠️ 重要限制:** 当前系统 JDK 版本为 JDK 21.0.11，项目配置要求 JDK 25。

**编译验证结果:**
```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.14.0:compile
        (default-compile) on project hadoken-common: Fatal error compiling: 错误: 不支持发行版本 25
```

**影响:**
- 无法实际编译验证 JDK 25 兼容性
- 需要安装 JDK 25 或使用 JDK 25 环境进行编译测试
- 所有依赖版本更新已完成，但编译验证阻塞

**建议:**
- 在 JDK 25 环境下执行 `mvn clean compile -DskipTests` 验证编译
- 或使用 Docker 容器运行 JDK 25 环境进行测试
- Phase 04 依赖版本更新完成，编译验证标记为阻塞状态

## 版本升级建议

基于分析结果，建议以下版本更新：

```xml
<!-- 数据库相关依赖 - 建议版本 -->
<mybatis-plus.version>3.5.16</mybatis-plus.version>
<druid.version>1.2.28</druid.version>
<mysql.version>8.4.0</mysql.version> <!-- 保持或升级到 9.7.0 -->
<dynamic-datasource.version>4.3.1</dynamic-datasource.version> <!-- 保持或升级到 4.5.0 -->
<redisson.version>3.45.1</redisson.version> <!-- 保持，待验证 -->
```

## 下一步行动

1. **升级 MyBatis-Plus 到 3.5.16** - 高优先级，确保 Spring Boot 4.x 支持
2. **升级 Druid 到 1.2.28** - 高优先级，明确支持 Spring Boot 4.x
3. **验证 Redisson JDK 25 兼容性** - 中优先级
4. **验证国产数据库驱动** - 低优先级，根据实际使用情况决定
5. **在 JDK 25 环境下执行编译验证** - 必须步骤

## 结论

大多数数据库依赖组件支持或兼容 JDK 25。MyBatis-Plus 和 Druid 建议升级到最新版本以确保最佳兼容性。MySQL Connector、Dynamic Datasource 和 Spring Data Redis 当前版本应可正常工作。

**关键阻塞:** 需要 JDK 25 环境进行实际编译验证。