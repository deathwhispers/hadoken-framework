package com.hadoken.jpa.base;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.lang.reflect.Field;
import java.sql.Timestamp;

/**
 * 通用字段
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/09 9:23
 */
@Getter
@Setter
// 标注为@MappedSuperclass的类将不是一个完整的实体类，他将不会映射到数据库表，但是他的属性都将映射到其子类的数据库字段中。
// 标注为@MappedSuperclass的类不能再标注@Entity或@Table注解，也无需实现序列化接口。
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseEntity {

/*    @Schema(description = "已删除", hidden = true)
    private Boolean delete;*/

    @CreatedBy
    @Column(name = "create_by", updatable = false)
    @Schema(description = "创建人", hidden = true)
    private String createBy;

    @LastModifiedBy
    @Column(name = "update_by")
    @Schema(description = "更新人", hidden = true)
    private String updateBy;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    @Schema(description = "创建时间", hidden = true)
    private Timestamp createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    @Schema(description = "更新时间", hidden = true)
    private Timestamp updateTime;

    /* 分组校验 */
    public @interface Create {
    }

    /* 分组校验 */
    public @interface Update {
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        Field[] fields = this.getClass().getDeclaredFields();
        try {
            for (Field f : fields) {
                f.setAccessible(true);
                builder.append(f.getName(), f.get(this)).append("\n");
            }
        } catch (Exception e) {
            builder.append("toString builder encounter an error");
        }
        return builder.toString();
    }
}
