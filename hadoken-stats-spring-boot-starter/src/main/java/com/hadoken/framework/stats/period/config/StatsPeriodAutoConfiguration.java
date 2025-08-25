package com.hadoken.framework.stats.period.config;

import com.hadoken.framework.stats.period.resolver.PeriodResolver;
import com.hadoken.framework.stats.period.resolver.StatsPeriodResolver;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * 统计周期解析器自动配置类
 *
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 10:47
 */
@AutoConfiguration
@EnableConfigurationProperties(StatsPeriodProperties.class)
public class StatsPeriodAutoConfiguration {


    @Bean
    public StatsPeriodResolver statsPeriodResolver(StatsPeriodProperties properties) throws Exception {

        // 自动加载内置实现
        Map<String, PeriodResolver> resolverMap = new LinkedHashMap<>(loadBuiltInResolvers());

        // 加载用户通过 SPI 提供的扩展
        ServiceLoader<PeriodResolver> loader = ServiceLoader.load(PeriodResolver.class);
        loader.iterator().forEachRemaining(resolver -> {
            String type = inferType(resolver);
            if (!resolverMap.containsKey(type)) {
                resolverMap.put(type, resolver);
            }
        });

        // 配置过滤
        Set<String> enabledTypes = properties.getEnabledTypes();
        if (enabledTypes != null && !enabledTypes.isEmpty()) {
            resolverMap.keySet().retainAll(enabledTypes);
        }

        ZoneId zoneId = ZoneId.of(properties.getZoneId());
        return new StatsPeriodResolver(resolverMap, zoneId);
    }

    private String inferType(PeriodResolver resolver) {
        String className = resolver.getClass().getSimpleName();
        if (className.endsWith("Resolver")) {
            String baseName = className.substring(0, className.length() - 8);
            return Character.toLowerCase(baseName.charAt(0)) + baseName.substring(1);
        }
        return className;
    }

    private Map<String, PeriodResolver> loadBuiltInResolvers() throws Exception {
        Map<String, PeriodResolver> map = new LinkedHashMap<>();

        // 扫描的包
        String basePackage = PeriodResolver.class.getPackage().getName();

        // 创建扫描器（不使用默认过滤器）
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        // 添加过滤器：只扫描 PeriodResolver 的实现类
        scanner.addIncludeFilter(new AssignableTypeFilter(PeriodResolver.class));

        // 扫描指定包
        for (BeanDefinition bd : scanner.findCandidateComponents(basePackage)) {
            try {
                Class<?> clazz = Class.forName(bd.getBeanClassName());
                if (PeriodResolver.class.isAssignableFrom(clazz)
                        && !clazz.isInterface()
                        && !java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                    PeriodResolver resolver = (PeriodResolver) clazz.getDeclaredConstructor().newInstance();
                    String type = inferType(resolver);
                    map.put(type, resolver);
                }
            } catch (Exception e) {
                throw new IllegalStateException("Failed to instantiate PeriodResolver: " + bd.getBeanClassName(), e);
            }
        }
        return map;
    }

}
