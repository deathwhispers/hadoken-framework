package com.hadoken.framework.scheduler.store.mybatis;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务执行日志的MyBatis-Plus Mapper接口。
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/22 11:00
 */
@Mapper
public interface TaskLogMapper extends BaseMapper<TaskLogEntity> {
}
