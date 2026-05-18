package com.github.hadoken.framework.monitor.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * BizTracer配置类
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/02 10:51
 */
@ConfigurationProperties("hadoken.trace")
@Data
public class TraceProperties {
}
