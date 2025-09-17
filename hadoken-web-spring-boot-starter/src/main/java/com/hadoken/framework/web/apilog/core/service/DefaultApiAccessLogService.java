package com.hadoken.framework.web.apilog.core.service;

import com.hadoken.framework.web.apilog.core.service.dto.ApiAccessLogDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/9/16 14:23
 */
@Slf4j
public class DefaultApiAccessLogService implements ApiAccessLogService {
    @Override
    public void createApiAccessLogAsync(ApiAccessLogDTO createDTO) {
        log.debug("默认 api 访问日志实现 >> {}", createDTO);
    }
}
