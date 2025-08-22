package com.hadoken.framework.scheduler.endpoint;

import com.hadoken.framework.scheduler.config.HadokenSchedulerProperties;
import com.hadoken.framework.scheduler.store.TaskLogStore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 定时任务日志管理
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/22 10:29
 */
@Tag(name = "定时任务日志管理")
@Slf4j
@RestController
@RequestMapping("${hadoken.scheduler.endpoint.prefix:/api/scheduler}/logs")
public class SchedulerLogController {

    private final TaskLogStore taskLogStore;
    private final HadokenSchedulerProperties properties;

    public SchedulerLogController(TaskLogStore taskLogStore, HadokenSchedulerProperties properties) {
        this.taskLogStore = taskLogStore;
        this.properties = properties;
    }

    public record TaskExecutionLogDTO(LocalDateTime startTime, long durationMillis, boolean success,
                                      String errorMessage, String instanceId) {
    }


    @Operation(summary = "根据 taskId 查询日志")
    @GetMapping("/{taskId}")
    public ResponseEntity<List<TaskExecutionLogDTO>> getTaskLogs(@PathVariable String taskId,
                                                                 @RequestParam(defaultValue = "100") int size) {

        // 确保请求的日志数量不超过全局配置的最大值
        int finalSize = Math.min(size, properties.getLogRetentionSize());

        List<TaskExecutionLogDTO> logs = taskLogStore.findRecent(taskId, finalSize)
                .stream()
                .map(log -> new TaskExecutionLogDTO(
                        log.startTime(),
                        log.duration().toMillis(),
                        log.success(),
                        log.errorMessage(),
                        log.instanceId()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(logs);
    }
}
