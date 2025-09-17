package com.hadoken.framework.web.apilog.core.service;

import com.hadoken.framework.web.apilog.core.service.dto.ApiErrorLogDTO;
import jakarta.validation.Valid;


/**
 * API 错误日志 接口
 *
 * @author yanggj
 * @version 1.0.0
 */
public interface ApiErrorLogService {

    /**
     * 创建 API 错误日志
     *
     * @param createDTO 创建信息
     */
    void createApiErrorLogAsync(@Valid ApiErrorLogDTO createDTO);

}
