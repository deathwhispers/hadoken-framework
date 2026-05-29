/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.github.hadoken.common.util;

import com.github.hadoken.common.config.EncryptProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 加密工具类
 * <p>
 * 提供对称加密功能，已从DES升级为AES-256-GCM
 * 密钥通过配置属性 hadoken.security.encrypt.aes-key 注入
 *
 * @author Zheng Jie
 * @author yanggj
 * @date 2018-11-23
 * @version 2.0.0
 */
@Configuration
@Slf4j
public class EncryptUtils {

    private static final String DES_DEPRECATED_MESSAGE =
        "DES加密算法已弃用，请使用AES-256-GCM替代。密钥通过 hadoken.security.encrypt.aes-key 配置";

    private static EncryptProperties encryptProperties;
    private static byte[] aesKeyBytes;

    @Autowired
    public void setEncryptProperties(EncryptProperties encryptProperties) {
        EncryptUtils.encryptProperties = encryptProperties;
        init();
    }

    private static void init() {
        if (encryptProperties != null && encryptProperties.isEnabled()) {
            try {
                String aesKeyBase64 = encryptProperties.getAesKey();
                if (aesKeyBase64 == null || aesKeyBase64.trim().isEmpty()) {
                    throw new IllegalStateException("AES密钥未配置，请设置 hadoken.security.encrypt.aes-key");
                }
                aesKeyBytes = AesGcmUtil.decodeKeyFromBase64(aesKeyBase64);
                if (!AesGcmUtil.isValidAes256Key(aesKeyBytes)) {
                    throw new IllegalArgumentException(
                        String.format("AES密钥长度必须为32字节（256位），当前为%d字节", aesKeyBytes.length)
                    );
                }
                log.info("EncryptUtils初始化成功，使用AES-256-GCM加密");
            } catch (Exception e) {
                log.error("EncryptUtils初始化失败: {}", e.getMessage(), e);
                throw new IllegalStateException("加密工具初始化失败: " + e.getMessage(), e);
            }
        } else {
            log.warn("加密功能已禁用，EncryptUtils将无法使用");
        }
    }

    /**
     * 对称加密（AES-256-GCM）
     * <p>
     * 使用AES-256-GCM算法加密文本，IV随机生成并与密文一起返回。
     * 返回Base64编码的加密结果。
     *
     * @param source 明文文本
     * @return Base64编码的加密文本
     * @throws Exception 加密失败时抛出异常
     */
    public static String desEncrypt(String source) throws Exception {
        if (encryptProperties == null || !encryptProperties.isEnabled()) {
            throw new IllegalStateException("加密功能未初始化或已禁用");
        }
        if (aesKeyBytes == null) {
            throw new IllegalStateException("AES密钥未初始化，请检查配置");
        }
        if (source == null || source.trim().isEmpty()) {
            throw new IllegalArgumentException("加密文本不能为空");
        }

        return AesGcmUtil.encrypt(source, aesKeyBytes);
    }

    /**
     * 对称解密（AES-256-GCM）
     * <p>
     * 解密由 desEncrypt 方法加密的文本。
     *
     * @param source Base64编码的加密文本
     * @return 解密后的明文文本
     * @throws Exception 解密失败时抛出异常
     */
    public static String desDecrypt(String source) throws Exception {
        if (encryptProperties == null || !encryptProperties.isEnabled()) {
            throw new IllegalStateException("加密功能未初始化或已禁用");
        }
        if (aesKeyBytes == null) {
            throw new IllegalStateException("AES密钥未初始化，请检查配置");
        }
        if (source == null) {
            throw new IllegalArgumentException("解密文本不能为null");
        }
        if (source.trim().isEmpty()) {
            throw new IllegalArgumentException("解密文本不能为空字符串");
        }

        return AesGcmUtil.decrypt(source, aesKeyBytes);
    }

    /**
     * 已弃用：DES加密（仅用于向后兼容）
     * <p>
     * @deprecated DES算法已不安全，请使用 {@link #desEncrypt(String)} 方法
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static String deprecatedDesEncrypt(String source) throws Exception {
        log.warn(DES_DEPRECATED_MESSAGE);
        throw new UnsupportedOperationException(DES_DEPRECATED_MESSAGE);
    }

    /**
     * 已弃用：DES解密（仅用于向后兼容）
     * <p>
     * @deprecated DES算法已不安全，请使用 {@link #desDecrypt(String)} 方法
     */
    @Deprecated(since = "2.0.0", forRemoval = true)
    public static String deprecatedDesDecrypt(String source) throws Exception {
        log.warn(DES_DEPRECATED_MESSAGE);
        throw new UnsupportedOperationException(DES_DEPRECATED_MESSAGE);
    }

    /**
     * 字节数组转十六进制字符串（保留原有工具方法）
     */
    public static String byte2hex(byte[] inStr) {
        if (inStr == null) {
            return "";
        }
        String stmp;
        StringBuilder out = new StringBuilder(inStr.length * 2);
        for (byte b : inStr) {
            stmp = Integer.toHexString(b & 0xFF);
            if (stmp.length() == 1) {
                // 如果是0至F的单位字符串，则添加0
                out.append("0").append(stmp);
            } else {
                out.append(stmp);
            }
        }
        return out.toString();
    }

    /**
     * 十六进制字符串转字节数组（保留原有工具方法）
     */
    public static byte[] hex2byte(byte[] b) {
        int size = 2;
        if ((b.length % size) != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += size) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }
}