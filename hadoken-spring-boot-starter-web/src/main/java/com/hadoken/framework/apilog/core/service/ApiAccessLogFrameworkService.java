package com.hadoken.framework.apilog.core.service;

import com.hadoken.framework.apilog.core.service.dto.ApiAccessLogDTO;

import jakarta.validation.Valid;

/**
 * API 访问日志接口
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/02 9:28
 */
public interface ApiAccessLogFrameworkService {

    /**
     * 创建 API 访问日志
     *
     * @param createDTO 创建信息
     */
    void createApiAccessLogAsync(@Valid ApiAccessLogDTO createDTO);

}
