package com.hadoken.framework.security.core;

import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.hadoken.common.enums.CommonStatusEnum;
import com.hadoken.common.enums.UserTypeEnum;

import java.util.*;

/**
 * 登录用户信息
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/03/01 18:16
 */
@Data
public class LoginUser implements UserDetails {

    /**
     * 用户编号
     */
    private Long id;

    /**
     * 用户类型
     * <p>
     * 关联 {@link UserTypeEnum}
     */
    private Integer userType;

    /**
     * 最后更新时间
     */
    private Date updateTime;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 租户编号
     */
    private Long tenantId;

    /**
     * 角色编号数组
     */
    private Set<Long> roleIds;

    /**
     * 组编号
     */
    private Long groupId;

    // ========== 上下文 ==========
    /**
     * 上下文字段，不进行持久化
     * <p>
     * 1. 用于基于 LoginUser 维度的临时缓存
     */
    @JsonIgnore
    private Map<String, Object> context;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return CommonStatusEnum.ENABLE.getStatus().equals(status);
    }

    // ========== 上下文 ==========

    public void setContext(String key, Object value) {
        if (context == null) {
            context = new HashMap<>();
        }
        context.put(key, value);
    }

    public <T> T getContext(String key, Class<T> type) {
        return MapUtil.get(context, key, type);
    }

}
