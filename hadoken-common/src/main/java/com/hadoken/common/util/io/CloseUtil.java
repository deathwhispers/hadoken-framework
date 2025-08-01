package com.hadoken.common.util.io;

import java.io.Closeable;

/**
 * 用于关闭各种连接，缺啥补啥
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/02/28 15:51
 */
public class CloseUtil {

    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                // 静默关闭
            }
        }
    }

    public static void close(AutoCloseable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                // 静默关闭
            }
        }
    }
}
