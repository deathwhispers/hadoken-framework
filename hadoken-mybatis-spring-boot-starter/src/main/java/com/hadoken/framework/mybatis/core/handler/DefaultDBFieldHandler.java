package com.hadoken.framework.mybatis.core.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.hadoken.framework.mybatis.core.entity.BaseDO;
import com.hadoken.framework.web.mvc.core.util.WebUtils;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

/**
 * 通用参数填充实现类
 * <p>如果没有显式的对通用参数进行赋值，这里会对通用参数进行填充、赋值</p>
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/02/28 15:02
 */
public class DefaultDBFieldHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof BaseDO) {
            BaseDO baseDO = (BaseDO) metaObject.getOriginalObject();

            LocalDateTime current = LocalDateTime.now();

            // 创建时间为空，则以当前时间为插入时间
            if (Objects.isNull(baseDO.getCreateTime())) {
                baseDO.setCreateTime(current);
            }

            // 更新时间为空，则以当前时间为更新时间
            if (Objects.isNull(baseDO.getUpdateTime())) {
                baseDO.setUpdateTime(current);
            }

            Long userId = WebUtils.getLoginUserId();

            // 当前登录用户不为空，创建人为空，则当前登录用户为创建人
            if (Objects.nonNull(userId) && Objects.isNull(baseDO.getCreateBy())) {
                baseDO.setCreateBy(userId.toString());
            }

            // 当前登录用户不为空，更新人为空，则当前登录用户为更新人
            if (Objects.nonNull(userId) && Objects.isNull(baseDO.getUpdateBy())) {
                baseDO.setUpdateBy(userId.toString());
            }
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if (Objects.nonNull(metaObject)) {

            // 更新时间为空，则以当前时间为更新时间
            setFieldValByName("updateTime", new Date(), metaObject);

            // 当前登录用户不为空，更新人为空，则当前登录用户为更新人
            Long userId = WebUtils.getLoginUserId();
            if (Objects.nonNull(userId)) {
                setFieldValByName("updater", userId.toString(), metaObject);
            }
        }
    }
}
