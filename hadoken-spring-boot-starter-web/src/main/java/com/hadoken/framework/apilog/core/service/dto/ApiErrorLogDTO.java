package com.hadoken.framework.apilog.core.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

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
public class ApiErrorLogDTO {

    /**
     * 链路编号
     */
    @Schema(description = "链路编号")
    private String traceId;

    /**
     * 账号编号
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
     * 异常时间
     */
    @Schema(description = "异常时间")
    @NotNull(message = "异常时间不能为空")
    private LocalDateTime exceptionTime;

    /**
     * 异常名
     */
    @Schema(description = "异常名")
    @NotNull(message = "异常名不能为空")
    private String exceptionName;

    /**
     * 异常发生的类全名
     */
    @Schema(description = "异常发生的类全名")
    @NotNull(message = "异常发生的类全名不能为空")
    private String exceptionClassName;

    /**
     * 异常发生的类文件
     */
    @Schema(description = "异常发生的类文件")
    @NotNull(message = "异常发生的类文件不能为空")
    private String exceptionFileName;

    /**
     * 异常发生的方法名
     */
    @Schema(description = "异常发生的方法名")
    @NotNull(message = "异常发生的方法名不能为空")
    private String exceptionMethodName;

    /**
     * 异常发生的方法所在行
     */
    @Schema(description = "异常发生的方法所在行")
    @NotNull(message = "异常发生的方法所在行不能为空")
    private Integer exceptionLineNumber;

    /**
     * 异常的栈轨迹异常的栈轨迹
     */
    @Schema(description = "异常的栈轨迹异常的栈轨迹")
    @NotNull(message = "异常的栈轨迹不能为空")
    private String exceptionStackTrace;

    /**
     * 异常导致的根消息
     */
    @Schema(description = "异常导致的根消息")
    @NotNull(message = "异常导致的根消息不能为空")
    private String exceptionRootCauseMessage;

    /**
     * 异常导致的消息
     */
    @Schema(description = "异常导致的消息")
    @NotNull(message = "异常导致的消息不能为空")
    private String exceptionMessage;

}
