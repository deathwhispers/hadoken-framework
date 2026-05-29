package com.github.hadoken.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 加密配置属性
 * <p>
 * 用于管理加密相关的密钥配置，避免硬编码密钥
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2026/05/29
 */
@ConfigurationProperties(prefix = "hadoken.security.encrypt")
@Data
public class EncryptProperties {

    /**
     * AES-256 对称加密密钥
     * <p>
     * 必须为32字节（256位）的Base64编码字符串
     */
    private String aesKey;

    /**
     * RSA 私钥
     * <p>
     * 用于RSA非对称加密的私钥，Base64编码
     */
    private String rsaPrivateKey;

    /**
     * RSA 公钥
     * <p>
     * 用于RSA非对称加密的公钥，Base64编码
     */
    private String rsaPublicKey;

    /**
     * 是否启用加密功能
     * <p>
     * 默认启用，设置为false可禁用所有加密操作
     */
    private boolean enabled = true;

    /**
     * 验证配置是否有效
     *
     * @throws IllegalStateException 如果必需配置缺失
     */
    public void validate() {
        if (enabled) {
            if (aesKey == null || aesKey.trim().isEmpty()) {
                throw new IllegalStateException("必需配置 hadoken.security.encrypt.aes-key 未设置");
            }
            if (rsaPrivateKey == null || rsaPrivateKey.trim().isEmpty()) {
                throw new IllegalStateException("必需配置 hadoken.security.encrypt.rsa-private-key 未设置");
            }
            if (rsaPublicKey == null || rsaPublicKey.trim().isEmpty()) {
                throw new IllegalStateException("必需配置 hadoken.security.encrypt.rsa-public-key 未设置");
            }
        }
    }
}