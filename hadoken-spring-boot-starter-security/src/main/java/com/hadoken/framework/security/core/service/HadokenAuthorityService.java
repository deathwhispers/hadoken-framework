package com.hadoken.framework.security.core.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * @author yanggj
 * @version 1.0.0
 * @date 2024/1/12 15:42
 */
@Service(value = "hadoken")
public class HadokenAuthorityService {

    public Boolean check(String ...permissions){
        // 获取当前用户的所有权限
//        List<String> elPermissions = SecurityUtils.getLoginUser().getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        // 判断当前用户的所有权限是否包含接口上定义的权限
//        return elPermissions.contains("admin") || Arrays.stream(permissions).anyMatch(elPermissions::contains);

        return true;
    }
}
