package com.hadoken.framework.scheduler.endpoint;

import com.hadoken.framework.scheduler.manager.TaskManager;
import com.hadoken.framework.scheduler.model.CreateTaskRequestDTO;
import com.hadoken.framework.scheduler.model.ManagedTask;
import com.hadoken.framework.scheduler.model.TaskDefinition;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * 为任务管理提供的 API 端点。
 *
 * @version 1.0.0
 * Created on 2025/8/22 09:09
 */
@Slf4j
@RestController
@RequestMapping("${hadoken.scheduler.endpoint.prefix:/api/scheduler}/tasks")
public class SchedulerController {

    private final TaskManager taskManager;

    public SchedulerController(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public record TaskDetailDTO(String id, String description, String sourceType, String triggerType,
                                String triggerValue, String status, LocalDateTime lastExecutionTime,
                                Long successCount, Long failureCount, Long avgExecutionTimeMillis,
                                LocalDateTime nextExecutionTime) {
    }

    @Operation(summary = "查询所有动态任务")
    @PostMapping("/list-all")
    public Collection<TaskDetailDTO> getAllTasks() {
        return taskManager.getAllTasks().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Operation(summary = "查询动态任务")
    @PostMapping("/tasks/{id}")
    public ResponseEntity<TaskDetailDTO> getTask(@PathVariable String id) {
        return taskManager.getTask(id)
                .map(this::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "开始任务")
    @PostMapping("/start/{id}")
    public ResponseEntity<Void> startTask(@PathVariable String id) {
        try {
            taskManager.start(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(summary = "停止任务")
    @PostMapping("/stop/{id}")
    public ResponseEntity<Void> stopTask(@PathVariable String id) {
        try {
            taskManager.stop(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(summary = "执行一次")
    @PostMapping("/trigger/{id}")
    public ResponseEntity<Void> triggerTask(@PathVariable String id) {
        try {
            taskManager.triggerOnce(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(summary = "创建一个动态任务")
    @PostMapping("/create")
    public ResponseEntity<TaskDetailDTO> createTask(@RequestBody @Valid CreateTaskRequestDTO requestDTO) {
        try {
            TaskDefinition definition = toDefinition(requestDTO);
            taskManager.createDynamicTask(definition);
            // 创建成功后，返回新创建任务的详细信息和 201 Created 状态
            return taskManager.getTask(definition.getId())
                    .map(this::toDto)
                    .map(dto -> ResponseEntity.status(HttpStatus.CREATED).body(dto))
                    .orElse(ResponseEntity.internalServerError().build());
        } catch (Exception e) {
            log.error("创建动态任务失败.", e);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @Operation(summary = "删除一个动态任务")
    @PostMapping("/delete/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        try {
            taskManager.deleteTask(id);
            // 删除成功，返回 204 No Content
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("删除任务失败，id '{}'.", id, e);
            return ResponseEntity.badRequest().build();
        }
    }

    private TaskDefinition toDefinition(CreateTaskRequestDTO dto) {
        return TaskDefinition.builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .beanName(dto.getBeanName())
                .methodName(dto.getMethodName())
                .triggerType(dto.getTriggerType())
                .triggerValue(dto.getTriggerValue())
                .build();
    }

    private TaskDetailDTO toDto(ManagedTask task) {
        TaskDefinition def = task.getDefinition();
        return new TaskDetailDTO(
                def.getId(), def.getDescription(), def.getSourceType().name(), def.getTriggerType().name(),
                def.getTriggerValue(), task.getStatus().name(), task.getLastExecutionTime(),
                task.getSuccessCount().get(), task.getFailureCount().get(), task.getAverageExecutionTimeMillis(),
                task.getNextExecutionTime()
        );
    }
}