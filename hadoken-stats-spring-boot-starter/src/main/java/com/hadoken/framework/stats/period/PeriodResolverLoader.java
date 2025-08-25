package com.hadoken.framework.stats.period;

import com.hadoken.framework.stats.period.resolver.PeriodResolver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author yanggj
 * @version 1.0.0
 * Created on 2025/8/11 11:19
 */
public class PeriodResolverLoader {

    private static final ServiceLoader<PeriodResolver> LOADER =
            ServiceLoader.load(PeriodResolver.class, PeriodResolverLoader.class.getClassLoader());

    private final Map<String, PeriodResolver> resolverMap = new ConcurrentHashMap<>();

    public PeriodResolverLoader() {
        reload();
    }

    /**
     * 重新加载所有 SPI 实现
     */
    public void reload() {
        List<PeriodResolver> resolvers = new ArrayList<>();
        LOADER.iterator().forEachRemaining(resolvers::add);

        // 按类名推断 type（小驼峰）
        Map<String, PeriodResolver> map = resolvers.stream()
                .collect(Collectors.toMap(
                        this::inferType,
                        resolver -> resolver,
                        (a, b) -> a // 冲突保留第一个
                ));

        this.resolverMap.clear();
        this.resolverMap.putAll(map);
    }

    /**
     * 推断 resolver 类型名（小驼峰）
     * 例如：TodayResolver -> "today"
     * LastNDaysResolver -> "lastNDays"
     */
    private String inferType(PeriodResolver resolver) {
        String className = resolver.getClass().getSimpleName();
        if (className.endsWith("Resolver")) {
            String baseName = className.substring(0, className.length() - 8);
            return Character.toLowerCase(baseName.charAt(0)) + baseName.substring(1);
        }
        return className;
    }

    /**
     * 获取所有注册的 resolver
     */
    public Collection<PeriodResolver> getAllResolvers() {
        return Collections.unmodifiableCollection(resolverMap.values());
    }

    /**
     * 根据 type 获取 resolver
     */
    public PeriodResolver getResolver(String type) {
        return resolverMap.get(type);
    }

    /**
     * 获取所有支持的 type
     */
    public Set<String> getSupportedTypes() {
        return Collections.unmodifiableSet(resolverMap.keySet());
    }
}
