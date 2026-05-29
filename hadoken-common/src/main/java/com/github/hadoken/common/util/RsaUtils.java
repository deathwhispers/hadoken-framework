package com.github.hadoken.common.util;

import com.github.hadoken.common.config.EncryptProperties;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA 工具类，公钥私钥生成，加解密
 * <p>
 * 已升级：密钥长度从1024位升级到2048位，使用java.util.Base64替换Tomcat Base64
 * 密钥通过EncryptProperties配置注入，移除硬编码测试数据
 *
 * @author https://www.cnblogs.com/nihaorz/p/10690643.html
 * @author yanggj
 * @date 2020-05-18
 * @version 2.0.0
 **/
public class RsaUtils {

    // 移除硬编码测试数据
    // private static final String SRC = "123456"; // 已移除

    /**
     * 默认RSA密钥长度（位）
     */
    public static final int DEFAULT_KEY_SIZE = 2048;

    /**
     * RSA算法名称
     */
    public static final String RSA_ALGORITHM = "RSA";

    /**
     * RSA算法填充模式
     */
    public static final String RSA_TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    /**
     * 已弃用：硬编码测试数据
     * @deprecated 使用外部测试数据，不在代码中硬编码
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    private static final String DEPRECATED_SRC = "123456";

    /**
     * 演示主方法（已更新，仅用于演示）
     * <p>
     * 更新：移除硬编码测试数据，使用生成的新密钥对
     */
    public static void main(String[] args) throws Exception {
        System.out.println("\n=== RSA工具类演示 ===");
        System.out.println("密钥长度: " + DEFAULT_KEY_SIZE + "位");
        System.out.println("使用Base64 API: java.util.Base64\n");

        RsaKeyPair keyPair = generateKeyPair();
        System.out.println("生成的公钥（Base64）：" + keyPair.getPublicKey());
        System.out.println("生成的私钥（Base64）：" + keyPair.getPrivateKey());
        System.out.println("\n");

        // 使用随机测试数据而非硬编码
        String testData = "RSA测试数据-" + System.currentTimeMillis();
        testEncryptionDecryption(keyPair, testData);
        System.out.println("\n=== 演示结束 ===\n");
    }

    /**
     * 测试加密解密（内部演示用）
     */
    private static void testEncryptionDecryption(RsaKeyPair keyPair, String testData) throws Exception {
        System.out.println("测试数据: " + testData);

        // 公钥加密
        String encrypted = encryptByPublicKey(keyPair.getPublicKey(), testData);
        System.out.println("公钥加密后: " + encrypted);

        // 私钥解密
        String decrypted = decryptByPrivateKey(keyPair.getPrivateKey(), encrypted);
        System.out.println("私钥解密后: " + decrypted);

        if (testData.equals(decrypted)) {
            System.out.println("✓ 加解密测试成功");
        } else {
            System.out.println("✗ 加解密测试失败");
        }

        // 私钥加密
        String encrypted2 = encryptByPrivateKey(keyPair.getPrivateKey(), testData);
        System.out.println("私钥加密后: " + encrypted2);

        // 公钥解密
        String decrypted2 = decryptByPublicKey(keyPair.getPublicKey(), encrypted2);
        System.out.println("公钥解密后: " + decrypted2);

        if (testData.equals(decrypted2)) {
            System.out.println("✓ 反向加解密测试成功");
        } else {
            System.out.println("✗ 反向加解密测试失败");
        }
    }

    /**
     * 公钥解密
     *
     * @param publicKeyText 公钥（Base64编码）
     * @param text          待解密的信息（Base64编码）
     * @return 解密后的文本
     * @throws Exception 解密失败时抛出异常
     */
    public static String decryptByPublicKey(String publicKeyText, String text) throws Exception {
        validateKeyAndText(publicKeyText, text);

        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyText));
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(Base64.getDecoder().decode(text));
        return new String(result);
    }

    /**
     * 私钥加密
     *
     * @param privateKeyText 私钥（Base64编码）
     * @param text           待加密的信息
     * @return Base64编码的加密文本
     * @throws Exception 加密失败时抛出异常
     */
    public static String encryptByPrivateKey(String privateKeyText, String text) throws Exception {
        validateKeyAndText(privateKeyText, text);

        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyText));
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(result);
    }

    /**
     * 私钥解密
     *
     * @param privateKeyText 私钥（Base64编码）
     * @param text           待解密的文本（Base64编码）
     * @return 解密后的文本
     * @throws Exception 解密失败时抛出异常
     */
    public static String decryptByPrivateKey(String privateKeyText, String text) throws Exception {
        validateKeyAndText(privateKeyText, text);

        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyText));
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(Base64.getDecoder().decode(text));
        return new String(result);
    }

    /**
     * 公钥加密
     *
     * @param publicKeyText 公钥（Base64编码）
     * @param text          待加密的文本
     * @return Base64编码的加密文本
     * @throws Exception 加密失败时抛出异常
     */
    public static String encryptByPublicKey(String publicKeyText, String text) throws Exception {
        validateKeyAndText(publicKeyText, text);

        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyText));
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance(RSA_TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] result = cipher.doFinal(text.getBytes());
        return Base64.getEncoder().encodeToString(result);
    }

    /**
     * 构建RSA密钥对
     *
     * @return RSA密钥对（包含Base64编码的公钥和私钥）
     * @throws NoSuchAlgorithmException 如果RSA算法不可用
     */
    public static RsaKeyPair generateKeyPair() throws NoSuchAlgorithmException {
        return generateKeyPair(DEFAULT_KEY_SIZE);
    }

    /**
     * 构建RSA密钥对（指定密钥长度）
     *
     * @param keySize 密钥长度（位），最小2048
     * @return RSA密钥对（包含Base64编码的公钥和私钥）
     * @throws NoSuchAlgorithmException 如果RSA算法不可用
     * @throws IllegalArgumentException 如果密钥长度小于2048
     */
    public static RsaKeyPair generateKeyPair(int keySize) throws NoSuchAlgorithmException {
        if (keySize < 2048) {
            throw new IllegalArgumentException(
                String.format("RSA密钥长度至少为2048位，当前为%d位", keySize)
            );
        }

        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        keyPairGenerator.initialize(keySize);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        String publicKeyString = Base64.getEncoder().encodeToString(rsaPublicKey.getEncoded());
        String privateKeyString = Base64.getEncoder().encodeToString(rsaPrivateKey.getEncoded());
        return new RsaKeyPair(publicKeyString, privateKeyString);
    }

    /**
     * 验证密钥和文本参数
     *
     * @param key  密钥（Base64编码）
     * @param text 文本
     * @throws IllegalArgumentException 如果参数无效
     */
    private static void validateKeyAndText(String key, String text) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("密钥不能为空");
        }
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("文本不能为空");
        }
    }

    /**
     * 从EncryptProperties获取RSA密钥对
     * <p>
     * 注意：此方法需要Spring上下文，适合在Spring管理的Bean中使用
     *
     * @param encryptProperties 加密配置属性
     * @return RSA密钥对（从配置读取）
     * @throws IllegalStateException 如果配置中缺少RSA密钥
     */
    public static RsaKeyPair getKeyPairFromConfig(EncryptProperties encryptProperties) {
        if (encryptProperties == null) {
            throw new IllegalArgumentException("EncryptProperties不能为空");
        }
        if (!encryptProperties.isEnabled()) {
            throw new IllegalStateException("加密功能已禁用");
        }

        String publicKey = encryptProperties.getRsaPublicKey();
        String privateKey = encryptProperties.getRsaPrivateKey();

        if (publicKey == null || publicKey.trim().isEmpty()) {
            throw new IllegalStateException("配置中缺少RSA公钥 (hadoken.security.encrypt.rsa-public-key)");
        }
        if (privateKey == null || privateKey.trim().isEmpty()) {
            throw new IllegalStateException("配置中缺少RSA私钥 (hadoken.security.encrypt.rsa-private-key)");
        }

        return new RsaKeyPair(publicKey, privateKey);
    }

    /**
     * RSA密钥对对象
     */
    public static class RsaKeyPair {

        private final String publicKey;
        private final String privateKey;

        public RsaKeyPair(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        @Override
        public String toString() {
            return String.format("RsaKeyPair{publicKey='%s...', privateKey='%s...'}",
                publicKey.substring(0, Math.min(20, publicKey.length())),
                privateKey.substring(0, Math.min(20, privateKey.length())));
        }
    }
}
