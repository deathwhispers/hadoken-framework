package com.hadoken.framework.scheduling.config;

import com.hadoken.framework.scheduling.manager.TaskManager;
import com.hadoken.framework.scheduling.model.CreateTaskRequestDTO;
import com.hadoken.framework.scheduling.model.ManagedTask;
import com.hadoken.framework.scheduling.model.TaskDefinition;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Web管理端点的自动配置。
 * 只有在满足多个条件时才会生效，确保了其可选性。
 * <p>
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/13 11:23
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(RestController.class)
@ConditionalOnProperty(name = "light.scheduler.management.enabled", havingValue = "true", matchIfMissing = true)
public class ScheduleManagerEndpointConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ScheduleManagementController schedulerManagementController(TaskManager taskManager) {
        return new ScheduleManagementController(taskManager);
    }

    /**
     * 为任务管理提供的RESTful API端点。
     */
    @Slf4j
    @RestController
    @RequestMapping("${light.scheduler.management.endpoint-prefix:/light-scheduler}")
    public static class ScheduleManagementController {

        private final TaskManager taskManager;

        public ScheduleManagementController(TaskManager taskManager) {
            this.taskManager = taskManager;
        }

        // DTOs for API responses
        public record TaskDetailDTO(String id, String description, String sourceType, String triggerType,
                                    String triggerValue, String status, Instant lastExecutionTime,
                                    Long successCount, Long failureCount, Long avgExecutionTimeMillis,
                                    Instant nextExecutionTime, List<TaskExecutionLogDTO> logs) {
        }

        public record TaskExecutionLogDTO(Instant startTime, long durationMillis, boolean success,
                                          String errorMessage, String instanceId) {
        }

        @GetMapping("/tasks")
        public Collection<TaskDetailDTO> getAllTasks() {
            return taskManager.getAllTasks().stream().map(this::toDto).collect(Collectors.toList());
        }

        @GetMapping("/tasks/{id}")
        public ResponseEntity<TaskDetailDTO> getTask(@PathVariable String id) {
            return taskManager.getTask(id)
                    .map(this::toDto)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        @PostMapping("/tasks/{id}/start")
        public ResponseEntity<Void> startTask(@PathVariable String id) {
            try {
                taskManager.start(id);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(null);
            }
        }

        @PostMapping("/tasks/{id}/stop")
        public ResponseEntity<Void> stopTask(@PathVariable String id) {
            try {
                taskManager.stop(id);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(null);
            }
        }

        @PostMapping("/tasks/{id}/trigger")
        public ResponseEntity<Void> triggerTask(@PathVariable String id) {
            try {
                taskManager.triggerOnce(id);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(null);
            }
        }

        /**
         * 创建一个动态任务。
         */
        @PostMapping("/tasks")
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
                log.error("Failed to create dynamic task.", e);
                // 可以在此根据异常类型返回更具体的错误码，如 409 Conflict
                return ResponseEntity.badRequest().body(null);
            }
        }

        /**
         * 删除一个任务。
         */
        @DeleteMapping("/tasks/{id}")
        public ResponseEntity<Void> deleteTask(@PathVariable String id) {
            try {
                taskManager.deleteTask(id);
                // 删除成功，返回 204 No Content
                return ResponseEntity.noContent().build();
            } catch (Exception e) {
                log.error("Failed to delete task with id '{}'.", id, e);
                return ResponseEntity.badRequest().build();
            }
        }

        // --- 新增一个DTO到内部模型的转换方法 ---
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
            List<TaskExecutionLogDTO> logDTOs = task.getExecutionLogs().stream()
                    .map(log -> new TaskExecutionLogDTO(log.startTime(), log.duration().toMillis(), log.success(), log.errorMessage(), log.instanceId()))
                    .collect(Collectors.toList());

            return new TaskDetailDTO(
                    def.getId(), def.getDescription(), def.getSourceType().name(), def.getTriggerType().name(),
                    def.getTriggerValue(), task.getStatus().name(), task.getLastExecutionTime(),
                    task.getSuccessCount().get(), task.getFailureCount().get(), task.getAverageExecutionTimeMillis(),
                    task.getNextExecutionTime(), logDTOs
            );
        }
    }
}