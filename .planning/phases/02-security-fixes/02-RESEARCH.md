# Phase 2: Security Fixes - Research

**Researched:** 2026-05-29
**Domain:** Java安全加密和密钥管理
**Confidence:** HIGH (基于项目代码分析和安全最佳实践)

## 摘要

本阶段需要解决项目中的严重安全漏洞，包括硬编码密钥、弱加密算法和不安全的默认配置。研究发现项目存在3个主要安全风险：1）EncryptUtils.java中使用已弃用的DES算法和硬编码密钥，2）RsaUtils.java中使用1024位弱RSA密钥，3）多个位置存在硬编码或默认密码。需要将这些不安全实现升级为符合现代安全标准的方案，同时保持API向后兼容性。

**主要推荐方案：**
- 将DES加密替换为AES-256-GCM
- 将RSA密钥长度从1024位升级到至少2048位
- 通过配置属性注入所有密钥，移除所有硬编码
- 创建专门的EncryptProperties配置类用于密钥管理

## 架构责任映射

| 能力 | 主要层 | 次要层 | 原理 |
|------|--------|--------|------|
| 对称加密 (AES/DES) | 公共层 (hadoken-common) | 配置层 | 提供基础加密工具，供其他模块调用 |
| 非对称加密 (RSA) | 公共层 (hadoken-common) | 配置层 | 提供RSA加解密工具 |
| 密钥管理 | 配置层 | Spring Boot配置 | 通过配置属性管理密钥 |
| 数据库字段加密 | MyBatis增强层 | 公共层 | 数据库层字段透明加密 |

## 标准技术栈

### 核心
| 库 | 版本 | 用途 | 为什么标准 |
|-----|------|------|------------|
| Java Cryptography Architecture | JDK内置 | 提供AES、RSA等加密算法 | Java标准库，无需额外依赖 |
| javax.crypto | JDK内置 | 对称和非对称加密实现 | Java标准加密API |
| Spring Boot Configuration | 3.3.13+ | 配置属性管理和注入 | Spring Boot标准配置机制 |
| @ConfigurationProperties | 3.3.13+ | 配置属性类定义 | Spring Boot标准属性绑定 |

### 支持库
| 库 | 版本 | 用途 | 何时使用 |
|-----|------|------|----------|
| java.util.Base64 | JDK 8+ | Base64编码解码 | 替换Tomcat Base64依赖 |
| jakarta.validation | 3.0.0+ | 配置属性验证 | 确保必填属性不为空 |

### 考虑的替代方案
| 替代方案 | 可使用 | 权衡 |
|----------|--------|------|
| BouncyCastle | 是 | 提供更多算法，但增加依赖复杂度 |
| jasypt-spring-boot | 是 | 支持配置加密，但当前需求简单 |
| Spring Security Crypto | 是 | 提供加密工具，但需要引入Spring Security依赖 |

**安装:**
```bash
# 无需额外安装 - 使用JDK内置加密库
```

## 架构模式

### 系统架构图

```
应用配置
    ↓
[EncryptProperties] ← 配置属性注入 (hadoken.security.encrypt.*)
    ↓
[EncryptUtils]     ← AES-256-GCM对称加密 (替换DES)
    ↓
[RsaUtils]         ← RSA 2048+非对称加密 (升级1024位)
    ↓
[DBAESUtil]        ← 数据库字段AES加密 (密钥外部化)
    ↓
[SecurityProperties] ← 移除默认密码"123456"
    ↓
配置验证 ← @NotEmpty检查必需属性
    ↓
应用启动 ← 必需配置缺失时拒绝启动
```

### 推荐项目结构
```
src/
├── main/
│   ├── java/com/github/hadoken/
│   │   └── common/
│   │       ├── config/                  # 配置类
│   │       │   ├── EncryptProperties.java      # 加密配置属性
│   │       │   └── RsaProperties.java          # 现有RSA配置
│   │       └── util/
│   │           ├── EncryptUtils.java           # 对称加密工具(升级后)
│   │           ├── RsaUtils.java               # RSA工具(升级后)
│   │           └── AesGcmUtil.java             # 新增AES-GCM工具
│   └── resources/
│       └── application.yml              # 配置文件
```

### 模式1: 配置属性注入模式
**什么：** 使用Spring Boot `@ConfigurationProperties`将密钥外部化到配置文件或环境变量
**何时使用：** 所有需要外部化的敏感配置
**示例：**
```java
// Source: 项目现有模式（RsaProperties.java）
@ConfigurationProperties(prefix = "hadoken.security.encrypt")
@Validated
@Data
public class EncryptProperties {
    @NotEmpty(message = "AES密钥不能为空")
    private String aesKey;
    
    @NotEmpty(message = "RSA私钥不能为空")  
    private String rsaPrivateKey;
    
    @NotEmpty(message = "RSA公钥不能为空")
    private String rsaPublicKey;
}
```

### 模式2: 密钥验证启动检查
**什么：** 应用启动时验证必需配置是否提供
**何时使用：** 安全相关的必需配置
**示例：**
```java
@Component
@ConditionalOnProperty(name = "hadoken.security.encrypt.enabled", havingValue = "true")
public class EncryptConfigValidator implements ApplicationRunner {
    
    @Autowired
    private EncryptProperties encryptProperties;
    
    @Override
    public void run(ApplicationArguments args) {
        if (StringUtils.isBlank(encryptProperties.getAesKey())) {
            throw new IllegalStateException("必需配置 hadoken.security.encrypt.aes-key 未设置");
        }
    }
}
```

### 反模式避免
- **硬编码密钥：** 密钥必须外部化，不能写在代码中
- **弱加密算法：** 避免使用DES、3DES、RC4等已弃用算法
- **固定IV：** AES-CBC模式必须使用随机IV，不能固定
- **打印堆栈跟踪：** 使用`log.error()`而不是`e.printStackTrace()`

## 不要手写

| 问题 | 不要构建 | 使用替代方案 | 为什么 |
|------|----------|--------------|--------|
| 密钥生成 | 不要手写密钥生成逻辑 | 使用`KeyGenerator.getInstance("AES")`或`KeyPairGenerator.getInstance("RSA")` | JDK内置API更安全，经过充分测试 |
| Base64编码 | 不要使用Tomcat的Base64 | 使用`java.util.Base64` | 标准Java API，无需额外依赖 |
| 随机数生成 | 不要使用`Math.random()` | 使用`SecureRandom.getInstanceStrong()` | 密码学安全的随机数生成器 |
| 密钥长度管理 | 不要手动计算密钥长度 | 使用算法标准长度(AES-256=32字节, RSA-2048=2048位) | 避免密钥长度错误 |

**关键洞察：** 加密实现容易出错，使用经过验证的Java标准库API，避免自定义加密算法实现。

## 运行时状态清单

> 此阶段不涉及重命名/重构/迁移，本节省略。

## 环境可用性

| 依赖 | 需要于 | 是否可用 | 版本 | 备选方案 |
|------|--------|----------|------|----------|
| Java JDK | 所有加密功能 | ✓ | 25 (目标版本) | — |
| Spring Boot | 配置管理 | ✓ | 3.3.13 (当前) | — |
| Maven | 构建 | ✓ | 3.6.3 (当前) | — |
| 加密算法 | DES替换为AES | ✓ | JDK内置 | — |
| 测试框架 | 单元测试 | ✗ | 无 | JUnit 5 + Mockito |

**缺少且无备选的依赖：**
- 单元测试框架需要安装

**缺少但有备选的依赖：**
- 无

## 验证架构

### 测试框架
| 属性 | 值 |
|------|-----|
| Framework | JUnit 5 + Mockito |
| Config file | pom.xml (添加依赖) |
| Quick run command | `mvn test -Dtest=EncryptUtilsTest` |
| Full suite command | `mvn test` |

### 阶段需求 → 测试映射
| 需求ID | 行为 | 测试类型 | 自动化命令 | 文件存在？ |
|--------|------|----------|------------|------------|
| SEC-01 | 硬编码密钥替换为配置注入 | 单元测试 | `mvn test -Dtest=EncryptPropertiesTest` | ❌ Wave 0 |
| SEC-02 | DES替换为AES-256-GCM | 单元测试 | `mvn test -Dtest=EncryptUtilsTest#testAesEncryption` | ❌ Wave 0 |
| SEC-03 | RSA密钥升级到2048位 | 单元测试 | `mvn test -Dtest=RsaUtilsTest#testRsaKeyLength` | ❌ Wave 0 |
| SEC-04 | 默认密码移除 | 单元测试 | `mvn test -Dtest=SecurityPropertiesTest` | ❌ Wave 0 |

### 采样率
- **每个任务提交：** `mvn test -Dtest=*Test`
- **每个Wave合并：** `mvn test`
- **阶段门禁：** 完整测试套件通过前进行`/gsd-verify-work`

### Wave 0 缺口
- [ ] `hadoken-common/src/test/java/com/github/hadoken/common/util/EncryptUtilsTest.java` — 覆盖SEC-02
- [ ] `hadoken-common/src/test/java/com/github/hadoken/common/util/RsaUtilsTest.java` — 覆盖SEC-03
- [ ] `hadoken-common/src/test/java/com/github/hadoken/common/config/EncryptPropertiesTest.java` — 覆盖SEC-01
- [ ] `hadoken-security-spring-boot-starter/src/test/java/com/github/hadoken/framework/security/autoconfigure/SecurityPropertiesTest.java` — 覆盖SEC-04
- [ ] `pom.xml` — 添加JUnit 5和Mockito依赖
- [ ] 框架安装：`mvn clean compile test` — 如果未检测到测试

*(如果没有缺口："现有测试基础设施覆盖所有阶段需求")*

## 常见陷阱

### 陷阱1: DES弃用风险
**什么出错：** JDK 21+已弃用DES算法，JDK 25可能完全移除。当前代码使用DES，升级后会出现`NoSuchAlgorithmException`
**为什么发生：** DES是56位对称密码，自1998年起被认为不安全
**如何避免：** 使用AES-256-GCM替换DES，支持认证加密
**警告标志：** `Cipher.getInstance("DES")`调用，硬编码密钥"Passw0rd"

### 陷阱2: RSA 1024位密钥不兼容
**什么出错：** RSA密钥小于2048位在新JDK中被默认禁用
**为什么发生：** NIST于2013年弃用1024位RSA
**如何避免：** 生成新的2048位或4096位RSA密钥对，升级RsaUtils默认长度
**警告标志：** `keyPairGenerator.initialize(1024)`调用

### 陷阱3: Tomcat Base64依赖
**什么出错：** 代码使用`org.apache.tomcat.util.codec.binary.Base64`，这是Tomcat特定依赖
**为什么发生：** 历史原因，当时Java标准库没有Base64支持
**如何避免：** 替换为`java.util.Base64`（Java 8+标准API）
**警告标志：** 导入`org.apache.tomcat.util.codec.binary.Base64`

### 陷阱4: 硬编码加密密钥
**什么出错：** 密钥硬编码在源代码中，暴露在版本控制中
**为什么发生：** 开发便利，从未迁移到适当的密钥管理
**如何避免：** 将所有密钥移至环境变量，使用配置属性注入
**警告标志：** 字符串字面量"Passw0rd"、"rhy"、"123456"

### 陷阱5: 固定IV使用
**什么出错：** `DBAESUtil.java`使用固定IV值`"6859505890402435"`
**为什么发生：** 简单实现但降低安全性
**如何避免：** 使用随机IV，将IV与密文一起存储
**警告标志：** `IvParameterSpec`使用固定字节数组

### 陷阱6: 异常处理不当
**什么出错：** `e.printStackTrace()`在`DefaultEncryptor.java`和`DefaultDecryptor.java`中使用
**为什么发生：** 未实现适当的日志记录
**如何避免：** 使用`log.error()`替代，添加适当的错误处理
**警告标志：** `e.printStackTrace()`调用

## 代码示例

来自官方来源的已验证模式：

### AES-256-GCM加密（JDK标准实现）
```java
// Source: Java Cryptography Architecture (JCA) 官方文档
public class AesGcmUtil {
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    
    public static String encrypt(String plaintext, byte[] key) throws Exception {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, parameterSpec);
        
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] encrypted = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, encrypted, 0, iv.length);
        System.arraycopy(ciphertext, 0, encrypted, iv.length, ciphertext.length);
        
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    public static String decrypt(String ciphertext, byte[] key) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(ciphertext);
        byte[] iv = Arrays.copyOfRange(decoded, 0, IV_LENGTH_BYTE);
        byte[] encrypted = Arrays.copyOfRange(decoded, IV_LENGTH_BYTE, decoded.length);
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec, parameterSpec);
        
        byte[] plaintext = cipher.doFinal(encrypted);
        return new String(plaintext, StandardCharsets.UTF_8);
    }
}
```

### RSA 2048位密钥生成
```java
// Source: Java Cryptography Architecture (JCA) 官方文档
public static RsaKeyPair generateKeyPair() throws NoSuchAlgorithmException {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    keyPairGenerator.initialize(2048); // 最小安全标准2048位
    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    
    RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
    RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
    
    String publicKeyString = Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded());
    String privateKeyString = Base64.getEncoder().encodeToString(rsaPrivateKey.getEncoded());
    
    return new RsaKeyPair(publicKeyString, privateKeyString);
}
```

## 最新技术状态

| 旧方法 | 当前方法 | 变更时间 | 影响 |
|--------|----------|----------|------|
| DES算法 | AES-256-GCM | JDK 8+ | DES已弃用，AES是当前标准 |
| RSA 1024位 | RSA 2048+位 | NIST 2013 | 1024位RSA不再安全 |
| Tomcat Base64 | java.util.Base64 | Java 8 | 标准API，无需外部依赖 |
| 硬编码密钥 | 配置注入 | 现代安全实践 | 避免密钥泄露 |
| e.printStackTrace() | log.error() | 日志最佳实践 | 更好的错误跟踪 |

**已弃用/过时：**
- `javax.crypto.Cipher.getInstance("DES")`：使用`"AES/GCM/NoPadding"`
- `KeyPairGenerator.getInstance("RSA").initialize(1024)`：使用2048或4096
- `org.apache.tomcat.util.codec.binary.Base64`：使用`java.util.Base64`

## 假设日志

> 本研究中所有`[ASSUMED]`标记的声明列表。规划器和讨论阶段使用此部分识别需要用户确认的决策。

| # | 声明 | 章节 | 错误风险 |
|---|------|------|----------|
| A1 | JDK 25完全支持AES-256-GCM算法 | 标准技术栈 | 如果JDK 25对GCM支持有问题，可能需要使用CBC模式 |
| A2 | Spring Boot 3.3.13配置属性机制足够用于密钥管理 | 架构模式 | 如果需要更复杂的密钥管理（如密钥轮换），需要额外设计 |
| A3 | 现有代码调用方仅使用`EncryptUtils.desEncrypt()`和`desDecrypt()`方法 | 代码示例 | 如果有其他方法或私有方法使用DES，需要额外修改 |
| A4 | 没有其他硬编码密钥隐藏在代码中 | 常见陷阱 | 可能存在未被grep找到的隐藏密钥 |
| A5 | 32字节AES-256密钥对现有使用足够 | 标准技术栈 | 如果现有数据使用不同的密钥长度，需要迁移策略 |

**如果此表为空：** 本研究中的所有声明都已验证或引用 — 无需用户确认。

## 开放问题

1. **密钥迁移策略**
   - 已知情况：现有使用DES加密的数据无法用AES解密
   - 不清楚：是否需要数据迁移？还是只影响未来数据？
   - 推荐：如果DES加密数据需要保留，实现双向兼容性（检测旧格式并迁移）

2. **密钥存储安全性**
   - 已知情况：密钥将从配置注入，但配置本身需要保护
   - 不清楚：是否应该使用jasypt或类似工具加密配置中的密钥？
   - 推荐：建议使用环境变量或专用密钥管理服务

## 安全域

### 适用的ASVS类别

| ASVS类别 | 适用 | 标准控制 |
|----------|------|----------|
| V2 认证 | 否 | 不适用 |
| V3 会话管理 | 否 | 不适用 |
| V4 访问控制 | 否 | 不适用 |
| V5 输入验证 | 是 | 配置属性使用`@NotEmpty`验证 |
| V6 密码学 | 是 | AES-256-GCM、RSA-2048、Java标准库 |

### {技术栈}的已知威胁模式

| 模式 | STRIDE | 标准缓解 |
|------|--------|----------|
| 硬编码密钥 | 信息披露 | 配置注入、环境变量 |
| 弱算法(DES) | 信息泄露 | 升级到AES-256-GCM |
| 密钥长度不足(RSA-1024) | 暴力破解 | 升级到RSA-2048+ |
| 固定IV | 模式分析攻击 | 使用随机IV |
| 默认密码 | 凭据猜测 | 移除默认值，要求显式配置 |

## 来源

### 主要（高置信度）
- Java Cryptography Architecture (JCA) 官方文档 - AES/GCM模式实现模式
- Spring Boot @ConfigurationProperties 文档 - 配置属性模式
- 项目代码分析 - 现有安全漏洞识别

### 次要（中置信度）
- NIST SP 800-131A标准 - RSA密钥长度建议（2048位最小）
- Java安全标准 - DES弃用时间线

### 三级（低置信度）
- [ASSUMED] JDK 25 AES-256-GCM兼容性 - 需要验证
- [ASSUMED] Spring Boot配置机制充分性 - 基于现有项目模式

## 元数据

**置信度细分：**
- 标准技术栈：HIGH - 基于Java标准库和Spring Boot标准机制
- 架构：HIGH - 项目已建立配置属性模式，只需扩展
- 陷阱：HIGH - 基于代码分析确认的具体问题

**研究日期：** 2026-05-29
**有效期至：** 30天（安全标准相对稳定）

---

## RESEARCH COMPLETE

**阶段：** 2 - 安全修复
**置信度：** HIGH

### 主要发现
1. **硬编码密钥问题：** 在3个文件中发现硬编码密钥：EncryptUtils.java("Passw0rd")、DBAESUtil.java("rhy")、SecurityProperties.java("123456")
2. **弱加密算法：** EncryptUtils.java使用已弃用的DES算法，RsaUtils.java使用1024位弱RSA密钥
3. **依赖问题：** RsaUtils.java使用Tomcat特定Base64，而非Java标准API
4. **配置模式：** 项目已建立`@ConfigurationProperties`模式，只需扩展用于密钥管理
5. **异常处理：** 多个文件使用`e.printStackTrace()`而非适当的日志记录

### 创建的文件
`.planning/phases/02-security-fixes/02-RESEARCH.md`

### 置信度评估
| 区域 | 级别 | 原因 |
|------|------|------|
| 标准技术栈 | HIGH | 基于Java标准库，模式明确 |
| 架构 | HIGH | 扩展现有配置属性模式 |
| 陷阱 | HIGH | 基于代码分析的具体问题 |

### 待解决问题
1. 现有DES加密数据是否需要迁移？
2. 配置中的密钥是否应该进一步加密？

### 可开始规划
研究完成。规划器现在可以创建PLAN.md文件。