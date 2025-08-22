package com.hadoken.framework.scheduler.store;

import com.hadoken.framework.scheduler.enums.TaskSourceType;
import com.hadoken.framework.scheduler.enums.TaskStatus;
import com.hadoken.framework.scheduler.enums.TriggerType;
import com.hadoken.framework.scheduler.model.TaskDefinition;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/22 09:37
 */
public class JdbcTaskStore implements TaskStore {

    private final JdbcTemplate jdbcTemplate;
    private final String tableName = "t_schedule_task_definition";

    private final RowMapper<TaskDefinition> rowMapper = new TaskDefinitionRowMapper();

    public JdbcTaskStore(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void save(TaskDefinition definition) {
        String sql = "INSERT INTO " + tableName + " (id, description, source_type, bean_name, method_name, trigger_type, trigger_value, status, lock_at_most_for_string) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                definition.getId(), definition.getDescription(), definition.getSourceType().name(),
                definition.getBeanName(), definition.getMethodName(), definition.getTriggerType().name(),
                definition.getTriggerValue(), definition.getStatus().name(), definition.getLockAtMostForString()
        );
    }

    @Override
    @Transactional
    public void update(TaskDefinition definition) {
        String sql = "UPDATE " + tableName + " SET description = ?, source_type = ?, bean_name = ?, method_name = ?, trigger_type = ?, trigger_value = ?, status = ?, lock_at_most_for_string = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                definition.getDescription(), definition.getSourceType().name(),
                definition.getBeanName(), definition.getMethodName(), definition.getTriggerType().name(),
                definition.getTriggerValue(), definition.getStatus().name(), definition.getLockAtMostForString(),
                definition.getId()
        );
    }

    @Override
    @Transactional
    public void updateStatus(String taskId, TaskStatus status) {
        String sql = "UPDATE " + tableName + " SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, status.name(), taskId);
    }

    @Override
    public Optional<TaskDefinition> findById(String taskId) {
        String sql = "SELECT * FROM " + tableName + " WHERE id = ?";
        List<TaskDefinition> results = jdbcTemplate.query(sql, rowMapper, taskId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public List<TaskDefinition> findAll() {
        String sql = "SELECT * FROM " + tableName;
        return jdbcTemplate.query(sql, rowMapper);
    }

    @Override
    @Transactional
    public void deleteById(String taskId) {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        jdbcTemplate.update(sql, taskId);
    }

    private static class TaskDefinitionRowMapper implements RowMapper<TaskDefinition> {
        @Override
        public TaskDefinition mapRow(ResultSet rs, int rowNum) throws SQLException {
            return TaskDefinition.builder()
                    .id(rs.getString("id"))
                    .description(rs.getString("description"))
                    .sourceType(TaskSourceType.valueOf(rs.getString("source_type")))
                    .beanName(rs.getString("bean_name"))
                    .methodName(rs.getString("method_name"))
                    .triggerType(TriggerType.valueOf(rs.getString("trigger_type")))
                    .triggerValue(rs.getString("trigger_value"))
                    .status(TaskStatus.valueOf(rs.getString("status")))
                    .lockAtMostForString(rs.getString("lock_at_most_for_string"))
                    .build();
        }
    }
}
