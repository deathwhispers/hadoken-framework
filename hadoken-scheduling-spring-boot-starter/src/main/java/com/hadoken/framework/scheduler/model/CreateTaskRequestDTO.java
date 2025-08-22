package com.hadoken.framework.scheduler.model;

import com.hadoken.framework.scheduler.enums.TriggerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 通过API动态创建任务的请求体 DTO.
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/14 20:11
 */
@Data
public class CreateTaskRequestDTO {

    @NotBlank(message = "任务ID不能为空")
    private String id;

    private String description;

    @NotBlank(message = "执行任务的Bean名称不能为空")
    private String beanName;

    @NotBlank(message = "执行任务的方法名不能为空")
    private String methodName;

    @NotNull(message = "触发器类型不能为空")
    private TriggerType triggerType;

    @NotBlank(message = "触发器具体的值不能为空")
    private String triggerValue;
}
