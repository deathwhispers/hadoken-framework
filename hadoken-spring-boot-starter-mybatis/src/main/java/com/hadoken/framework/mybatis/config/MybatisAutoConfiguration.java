package com.hadoken.framework.mybatis.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.hadoken.framework.mybatis.core.handler.DefaultDBFieldHandler;

/**
 * Mybatis 配置类
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/02/28 14:55
 */
@Configuration
// Mapper 懒加载，目前仅用于单元测试
@MapperScan(value = "${hadoken.info.base-package}", annotationClass = Mapper.class,
        lazyInitialization = "${mybatis.lazy-initialization:false}")
public class MybatisAutoConfiguration {

    /**
     * 配置分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 支持自动分页
        return new MybatisPlusInterceptor();
    }

    @Bean
    public MetaObjectHandler defaultMetaObjectHandler() {

        // 自动填充参数类
        return new DefaultDBFieldHandler();
    }

}
