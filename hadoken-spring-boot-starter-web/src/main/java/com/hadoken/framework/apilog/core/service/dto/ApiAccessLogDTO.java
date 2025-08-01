package com.hadoken.framework.apilog.core.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * API 错误日志创建 DTO
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/02 9:27
 */
@Schema(description = "API 错误日志")
@Data
@Accessors(chain = true)
public class ApiAccessLogDTO {

    /**
     * 链路追踪编号
     */
    @Schema(description = "链路编号")
    private String traceId;

    /**
     * 用户编号
     */
    @Schema(description = "账号编号")
    private Long userId;

    /**
     * 用户类型
     */
    @Schema(description = "用户类型")
    private Integer userType;

    /**
     * 应用名
     */
    @Schema(description = "应用名")
    @NotNull(message = "应用名不能为空")
    private String applicationName;

    /**
     * 请求方法名
     */
    @Schema(description = "请求方法名")
    @NotNull(message = "http 请求方法不能为空")
    private String requestMethod;

    /**
     * 访问地址
     */
    @Schema(description = "访问地址")
    @NotNull(message = "访问地址不能为空")
    private String requestUrl;

    /**
     * 请求参数
     */
    @Schema(description = "请求参数")
    private String requestParams;

    /**
     * 用户 IP
     */
    @Schema(description = "用户 IP")
    @NotNull(message = "ip 不能为空")
    private String userIp;

    /**
     * 浏览器 UA
     */
    @Schema(description = "浏览器 UA")
    private String userAgent;

    /**
     * 开始请求时间
     */
    @Schema(description = "开始请求时间")
    @NotNull(message = "开始请求时间不能为空")
    private Date beginTime;

    /**
     * 结束请求时间
     */
    @Schema(description = "结束请求时间")
    @NotNull(message = "结束请求时间不能为空")
    private Date endTime;

    /**
     * 执行时长，单位：毫秒
     */
    @Schema(description = "执行时长，单位：毫秒")
    @NotNull(message = "执行时长不能为空")
    private Integer duration;

    /**
     * 结果码
     */
    @Schema(description = "结果码")
    @NotNull(message = "结果码不能为空")
    private Integer resultCode;

    /**
     * 结果提示
     */
    @Schema(description = "结果提示")
    private String resultMsg;

}
