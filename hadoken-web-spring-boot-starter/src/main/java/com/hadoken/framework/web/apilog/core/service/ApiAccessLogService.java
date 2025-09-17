package com.hadoken.framework.web.apilog.core.service;

import com.hadoken.framework.web.apilog.core.service.dto.ApiAccessLogDTO;
import jakarta.validation.Valid;

/**
 * API 访问日志接口
 *
 * @author yanggj
 * @version 1.0.0
 */
public interface ApiAccessLogService {

    /**
     * 创建 API 访问日志
     *
     * @param createDTO 创建信息
     */
    void createApiAccessLogAsync(@Valid ApiAccessLogDTO createDTO);

}
