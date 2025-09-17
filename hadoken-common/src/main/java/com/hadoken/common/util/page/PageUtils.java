package com.hadoken.common.util.page;

import com.hadoken.common.entity.PageParam;

/**
 * {@link PageParam} 工具类
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/02/28 16:08
 */
public class PageUtils {

    public static int getStart(PageParam pageParam) {
        return (pageParam.getPageNo() - 1) * pageParam.getPageSize();
    }

}
