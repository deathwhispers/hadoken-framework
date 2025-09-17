package com.hadoken.framework.web.xss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

/**
 * Xss 配置属性
 *
 * @author yanggj
 */
@ConfigurationProperties(prefix = "hadoken.xss")
@Validated
@Data
public class XssProperties {

    /**
     * 是否开启，默认为 true
     */
    private boolean enable = true;
    /**
     * 需要排除的 URI，默认为空，支持通配符
     */
    private List<String> excludePatterns = Collections.emptyList();

}
