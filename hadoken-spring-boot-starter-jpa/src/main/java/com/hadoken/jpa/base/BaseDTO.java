package com.hadoken.jpa.base;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.lang.reflect.Field;
import java.sql.Timestamp;

/**
 * @author yanggj
 * @date 2022/03/09 9:23
 * @version 1.0.0
 */
@Getter
@Setter
public class BaseDTO {

/*    private Boolean delete;*/

    private String createBy;

    private String updateBy;

    private Timestamp createTime;

    private Timestamp updateTime;

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
