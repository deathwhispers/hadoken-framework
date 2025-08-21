package com.hadoken.framework.mybatis.core.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yanggj
 * @version 1.0.0
 * @date 2023/3/17 18:00
 */
public class PageHelper {


    /**
     * 将列表转为 page
     *
     * @param current 当前页
     * @param size    页大小
     * @param list    待处理数据列表
     * @param <T>     数据对象
     * @return {@link Page<T>}
     */
    public static <T> Page<T> listToPage(long current, long size, List<T> list) {
        Page<T> page = new Page<>(current, size);
        int start = (int) ((current - 1) * size);
        int end = (int) ((start + size) > list.size() ? list.size() : (size * current));
        page.setRecords(new ArrayList<>());
        page.setTotal(list.size());
        if (size * (current - 1) <= page.getTotal()) {
            page.setRecords(list.subList(start, end));
        }
        return page;
    }
}
