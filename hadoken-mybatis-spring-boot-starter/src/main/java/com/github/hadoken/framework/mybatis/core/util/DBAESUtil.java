package com.github.hadoken.framework.mybatis.core.util;

import com.github.hadoken.common.config.EncryptProperties;
import com.github.hadoken.common.util.AesGcmUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 数据库字段AES加密工具类
 * <p>
 * 用于数据库字段的透明加密解密。
 * 已升级：移除硬编码密钥和固定IV，通过EncryptProperties配置注入。
 * 建议使用AES-256-GCM模式（默认），也支持向后兼容的AES-CBC模式。
 *
 * @author yanggj
 * @version 2.0.0
 * @date 2022/8/25 10:19
 */
@Component
@Slf4j
public class DBAESUtil {

    /**
     * 加密算法模式
     */
    public enum AlgorithmMode {
        /** AES-GCM模式（推荐，提供认证加密） */
        GCM,
        /** AES-CBC模式（向后兼容） */
        CBC
    }

    @Autowired(required = false)
    private EncryptProperties encryptProperties;

    private byte[] dbEncryptionKey;
    private AlgorithmMode algorithmMode = AlgorithmMode.GCM;
    private boolean enabled = true;

    @PostConstruct
    public void init() {
        if (encryptProperties == null) {
            log.warn("EncryptProperties未配置，数据库字段加密功能将禁用");
            enabled = false;
            return;
        }

        if (!encryptProperties.isEnabled()) {
            log.info("加密功能全局禁用，数据库字段加密也将禁用");
            enabled = false;
            return;
        }

        try {
            String aesKeyBase64 = encryptProperties.getAesKey();
            if (aesKeyBase64 == null || aesKeyBase64.trim().isEmpty()) {
                throw new IllegalStateException("数据库加密密钥未配置，请设置 hadoken.security.encrypt.aes-key");
            }

            dbEncryptionKey = AesGcmUtil.decodeKeyFromBase64(aesKeyBase64);
            if (!AesGcmUtil.isValidAes256Key(dbEncryptionKey)) {
                throw new IllegalArgumentException(
                    String.format("数据库加密密钥必须为32字节（256位），当前为%d字节",
                        dbEncryptionKey == null ? 0 : dbEncryptionKey.length)
                );
            }

            // 检查是否配置了数据库专用加密模式
            String modeConfig = System.getProperty("hadoken.db.encryption.mode", "GCM");
            try {
                algorithmMode = AlgorithmMode.valueOf(modeConfig.toUpperCase());
            } catch (IllegalArgumentException e) {
                log.warn("未知的加密模式配置: {}, 使用默认GCM模式", modeConfig);
                algorithmMode = AlgorithmMode.GCM;
            }

            log.info("数据库字段加密初始化成功，模式: {}, 启用: {}", algorithmMode, enabled);
        } catch (Exception e) {
            log.error("数据库字段加密初始化失败: {}", e.getMessage(), e);
            enabled = false;
            throw new IllegalStateException("数据库字段加密初始化失败: " + e.getMessage(), e);
        }
    }

    /**
     * 加密数据库字段
     *
     * @param content 明文内容
     * @return Base64编码的加密内容，如果加密失败或禁用则返回原文
     */
    public String encrypt(String content) {
        if (!enabled || dbEncryptionKey == null) {
            log.debug("数据库加密功能禁用，返回原文");
            return content;
        }

        if (content == null) {
            return null;
        }

        try {
            switch (algorithmMode) {
                case GCM:
                    return encryptWithGcm(content);
                case CBC:
                    return encryptWithCbc(content);
                default:
                    throw new IllegalStateException("不支持的加密模式: " + algorithmMode);
            }
        } catch (Exception e) {
            log.error("数据库字段加密失败: {}", e.getMessage(), e);
            // 加密失败时返回原文（根据业务需求决定）
            return content;
        }
    }

    /**
     * 解密数据库字段
     *
     * @param content Base64编码的加密内容
     * @return 解密后的明文内容，如果解密失败或禁用则返回原文
     */
    public String decrypt(String content) {
        if (!enabled || dbEncryptionKey == null) {
            log.debug("数据库加密功能禁用，返回原文");
            return content;
        }

        if (content == null) {
            return null;
        }

        try {
            // 尝试检测加密模式
            if (isGcmEncrypted(content)) {
                return decryptWithGcm(content);
            } else {
                // 默认为CBC模式（向后兼容）
                return decryptWithCbc(content);
            }
        } catch (Exception e) {
            log.error("数据库字段解密失败: {}", e.getMessage(), e);
            // 解密失败时返回原文（可能是未加密的数据）
            return content;
        }
    }

    /**
     * 使用AES-GCM模式加密
     */
    private String encryptWithGcm(String content) throws Exception {
        return AesGcmUtil.encrypt(content, dbEncryptionKey);
    }

    /**
     * 使用AES-GCM模式解密
     */
    private String decryptWithGcm(String content) throws Exception {
        return AesGcmUtil.decrypt(content, dbEncryptionKey);
    }

    /**
     * 使用AES-CBC模式加密（向后兼容）
     */
    private String encryptWithCbc(String content) throws Exception {
        // 生成随机IV（16字节）
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        SecretKeySpec keySpec = new SecretKeySpec(dbEncryptionKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        byte[] encrypted = cipher.doFinal(content.getBytes());

        // 组合IV和密文：IV + 密文
        byte[] combined = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

        return Base64.getEncoder().encodeToString(combined);
    }

    /**
     * 使用AES-CBC模式解密（向后兼容）
     */
    private String decryptWithCbc(String content) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(content);

        // 提取IV（前16字节）和密文
        byte[] iv = new byte[16];
        byte[] encrypted = new byte[decoded.length - 16];
        System.arraycopy(decoded, 0, iv, 0, 16);
        System.arraycopy(decoded, 16, encrypted, 0, encrypted.length);

        SecretKeySpec keySpec = new SecretKeySpec(dbEncryptionKey, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] original = cipher.doFinal(encrypted);
        return new String(original);
    }

    /**
     * 检测是否为GCM加密的数据
     */
    private boolean isGcmEncrypted(String content) {
        try {
            byte[] decoded = Base64.getDecoder().decode(content);
            // GCM加密的数据包含12字节IV + 密文 + 16字节认证标签
            // 简单检查：长度足够且能正常解码
            return decoded.length >= 28; // 12字节IV + 至少1字节数据 + 16字节标签
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查加密功能是否启用
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * 获取当前加密模式
     */
    public AlgorithmMode getAlgorithmMode() {
        return algorithmMode;
    }

    /**
     * 已弃用：旧版加密方法（硬编码密钥）
     * @deprecated 使用配置注入的密钥
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static String deprecatedEncrypt(String content) throws Exception {
        log.warn("使用已弃用的硬编码密钥加密方法，请升级到配置注入版本");
        throw new UnsupportedOperationException("硬编码密钥加密方法已弃用，请使用配置注入版本");
    }

    /**
     * 已弃用：旧版解密方法（硬编码密钥）
     * @deprecated 使用配置注入的密钥
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static String deprecatedDecrypt(String content) throws Exception {
        log.warn("使用已弃用的硬编码密钥解密方法，请升级到配置注入版本");
        throw new UnsupportedOperationException("硬编码密钥解密方法已弃用，请使用配置注入版本");
    }
}