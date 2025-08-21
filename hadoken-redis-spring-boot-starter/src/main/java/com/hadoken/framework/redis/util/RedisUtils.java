package com.hadoken.framework.redis.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 工具类
 *
 * @author /
 */
@Slf4j
@SuppressWarnings({"unchecked", "all"})
public class RedisUtils {

    private RedisTemplate<String, Object> redisTemplate;
    private StringRedisTemplate stringRedisTemplate;

    public RedisUtils(RedisTemplate<String, Object> redisTemplate, StringRedisTemplate stringRedisTemplate) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     */
    public boolean expire(String key, long time) {
        return expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * 指定缓存失效时间
     *
     * @param key      键
     * @param time     时间
     * @param timeUnit 时间单位
     */
    public boolean expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                return stringRedisTemplate.expire(key, time, timeUnit);
            }
        } catch (Exception e) {
            log.error("设置过期时间失败: {}", key, e);
        }
        return false;
    }

    /**
     * 根据 key 获取过期时间
     *
     * @param key 键
     * @return 时间(秒) 返回0代表永久有效
     */
    public long getExpire(String key) {
        try {
            return stringRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("获取key过期时间失败: {}", key, e);
            return 0L;
        }
    }

    /**
     * 查找匹配指定 pattern 的所有 key
     *
     * @param pattern key 的匹配模式，例如 "user:*"
     * @return 匹配到的 key 列表
     */
    public List<String> scan(String pattern) {
        // 使用 execute 方法，让 Spring 自动管理 RedisConnection
        return stringRedisTemplate.execute((RedisConnection connection) -> {
            List<String> keys = new ArrayList<>();
            // 创建 ScanOptions，可以指定匹配模式和每次扫描的数量
            // 设置 count 是为了优化每次扫描的网络往返，推荐使用
            ScanOptions options = ScanOptions.scanOptions().match(pattern).count(1000).build();
            // 使用 try-with-resources 确保 Cursor 资源被正确关闭
            try (Cursor<byte[]> cursor = connection.scan(options)) {
                while (cursor.hasNext()) {
                    // 将 byte[] 转换为 String
                    keys.add(new String(cursor.next(), StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                // 打印错误日志，而不是手动释放连接
                // 在实际项目中，可以使用更完善的日志框架
                System.err.println("Error while scanning Redis keys: " + e.getMessage());
            }
            return keys;
        });
    }

    /**
     * 分页查询匹配 pattern 的 key
     * <p>
     * 注意：Redis 的 SCAN 命令不适合用于传统的分页（即直接跳到第 N 页）。
     * 此方法会一次性扫描出所有匹配的 key，然后在内存中进行分页。
     * 当匹配的 key 数量非常大时，可能会有性能问题或内存消耗过大。
     * * @param patternKey key 的匹配模式，例如 "user:*"
     *
     * @param page 页码 (从 0 开始)
     * @param size 每页数目
     * @return 当前页的 key 列表
     */
    public List<String> findKeysForPage(String patternKey, int page, int size) {
        if (page < 0 || size <= 0) {
            return Collections.emptyList();
        }

        // 使用之前实现的 scan 方法，获取所有匹配的 key
        List<String> allKeys = scan(patternKey);

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, allKeys.size());

        // 检查索引是否有效，避免抛出异常
        if (fromIndex >= allKeys.size() || fromIndex < 0) {
            return Collections.emptyList();
        }

        // 使用 List 的 subList 方法高效地获取指定页的数据
        return allKeys.subList(fromIndex, toIndex);
    }

    /**
     * 判断 key 是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return stringRedisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("判断key存在失败: {}", key, e);
            return false;
        }
    }

    /**
     * 删除缓存
     *
     * @param keys 可以传一个值 或多个
     */
    public void del(String... keys) {
        if (keys == null || keys.length == 0) return;

        if (keys.length == 1) {
            Boolean result = stringRedisTemplate.delete(keys[0]);
            log.debug("成功删除缓存：{}, 结果: {}", keys[0], result);
        } else {
            Set<String> keySet = new HashSet<>(Arrays.asList(keys));
            long count = stringRedisTemplate.delete(keySet);
            log.debug("成功删除缓存：{}, 缓存删除数量:{}", keySet, count);
        }
    }

    /**
     * 按前缀和ID批量删除
     * 如：删除设备的缓存，rhy:device:{id},则 prefix = rhy:device:, id = xxx
     */
    public void delByKeys(String prefix, Set<Long> ids) {
        Set<String> keys = new HashSet<>();
        for (Long id : ids) {
            keys.add(prefix + id);
        }
        stringRedisTemplate.delete(keys);
    }

    // ===================== 字符串操作（使用 StringRedisTemplate）=====================

    public String getString(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public boolean setString(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("设置字符串值失败", e);
            return false;
        }
    }

    /**
     * 设置过期时间
     *
     * @param time 默认单位：s
     */
    public boolean setString(String key, String value, long time) {
        return setString(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 设置过期时间
     *
     * @param time     过期时间
     * @param timeUnit 时间单位
     */
    public boolean setString(String key, String value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                stringRedisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                stringRedisTemplate.opsForValue().set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("设置字符串值失败: {}", key, e);
            return false;
        }
    }

    /**
     * 字符串递增
     *
     * @param key 键
     * @param by  要增加几(大于0)
     * @return 递增后的值
     */
    public long incr(String key, long by) {
        return stringRedisTemplate.opsForValue().increment(key, by);
    }

    /**
     * 字符串递减
     *
     * @param key 键
     * @param by  要减少几(大于0)
     * @return 递减后的值
     */
    public long decr(String key, long by) {
        return stringRedisTemplate.opsForValue().decrement(key, by);
    }

    // ===================== 对象操作（使用 RedisTemplate）=====================

    /**
     * 获取对象值
     *
     * @param key 键
     * @return T
     */
    public <T> T getObject(String key) {
        try {
            Object obj = redisTemplate.opsForValue().get(key);
            if (obj == null) {
                return null;
            }
            return (T) obj;
        } catch (Exception e) {
            log.error("获取对象失败: {}", key, e);
            return null;
        }
    }

    public <T> boolean setObject(String key, T value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("设置对象值失败", e);
            return false;
        }
    }

    /**
     * 设置对象值
     *
     * @param key   键
     * @param value 值
     * @param time  过期时间（秒），0或负数代表永久有效
     * @param <T>   对象的类型
     * @return true 成功 false 失败
     */
    public <T> boolean setObject(String key, T value, long time) {
        return setObject(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 设置对象值
     *
     * @param key      键
     * @param value    值
     * @param time     过期时间，0或负数代表永久有效
     * @param timeUnit 时间单位
     * @param <T>      对象的类型
     * @return true 成功 false 失败
     */
    public <T> boolean setObject(String key, T value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("设置对象值失败: {}", key, e);
            return false;
        }
    }

    // ================================ Hash 操作 =================================

    /**
     * 获取哈希表中的值
     *
     * @param key  键
     * @param item 项
     * @return 值
     */
    public <T> T hashGet(String key, String item) {
        try {
            Object obj = redisTemplate.opsForHash().get(key, item);
            if (obj == null) {
                return null;
            }
            return (T) obj;
        } catch (Exception e) {
            log.error("获取哈希表中的值失败: key={}, item={}", key, item, e);
            return null;
        }
    }

    /**
     * 获取哈希表中的所有键值对
     *
     * @param key 键
     * @return 对应的多个键值对
     */
    public <K, V> Map<K, V> hashMultiGet(String key) {

        try {
            Map<Object, Object> map = redisTemplate.opsForHash().entries(key);
            if (map.isEmpty()) {
                return Map.of();
            }
            return (Map<K, V>) map;
        } catch (Exception e) {
            log.error("获取哈希表失败: key={}", key, e);
            return Map.of();
        }
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public <T> boolean hashMultiSet(String key, Map<String, T> map) {
        return hashMultiSet(key, map, 0L);
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public <T> boolean hashMultiSet(String key, Map<String, T> map, long time) {
        return hashMultiSet(key, map, time, TimeUnit.SECONDS);
    }

    /**
     * HashSet 并设置时间
     *
     * @param key      键
     * @param map      对应多个键值
     * @param time     时间
     * @param timeUnit 时间单位
     * @return true成功 false失败
     */
    public <T> boolean hashMultiSet(String key, Map<String, T> map, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public <T> boolean hashSet(String key, String item, T value) {
        return hashSet(key, item, value, 0L);
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public <T> boolean hashSet(String key, String item, T value, long time) {
        return hashSet(key, item, value, time, TimeUnit.SECONDS);
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key      键
     * @param item     项
     * @param value    值
     * @param time     时间 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @param timeUnit 时间单位
     * @return true 成功 false失败
     */
    public <T> boolean hashSet(String key, String item, T value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                return expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error("设置哈希表中的值失败: key={}, item={}, value={}", key, item, value, e);
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public long hashDel(String key, Object... item) {
        long result = redisTemplate.opsForHash().delete(key, item);
        log.debug("删除缓存 >> key={}, item={}", key, item);
        return result;
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hashHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key  键
     * @param item 项
     * @param by   要增加几(大于0)
     * @return /
     */
    public Long hashIncr(String key, String item, long by) {
        try {
            return redisTemplate.opsForHash().increment(key, item, by);
        } catch (Exception e) {
            log.error("哈希递增失败: key={}, item={}, by={}", key, item, by, e);
            return null;
        }
    }

    /**
     * hash递减
     *
     * @param key  键
     * @param item 项
     * @param by   要减少记(小于0)
     * @return /
     */
    public Long hashDecr(String key, String item, long by) {
        try {
            return redisTemplate.opsForHash().increment(key, item, -by);
        } catch (Exception e) {
            log.error("哈希递减失败: key={}, item={}, by={}", key, item, by, e);
            return null;
        }
    }

    // ============================ Set 操作 =============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return {@link Set}
     */
    public <T> Set<T> setGet(String key) {
        try {
            Set<Object> set = redisTemplate.opsForSet().members(key);
            if (set == null || set.isEmpty()) {
                return Set.of();
            } else {
                return (Set<T>) set;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Set.of();
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean setHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long setSet(String key, Object... values) {
        return setSetExpire(key, 0L, values);
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long setSetExpire(String key, long time, Object... values) {
        return setSetExpire(key, time, TimeUnit.SECONDS, values);
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long setSetExpire(String key, long time, TimeUnit timeUnit, Object... values) {
        try {
            long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time, timeUnit);
            }
            return count;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return Set 的长度
     */
    public long setGetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return 0L;
        }
    }

    /**
     * 移除 Set 中指定的值
     *
     * @param key    键
     * @param values 值，可以是一个或多个
     * @return 移除的个数
     */
    public long setRemove(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().remove(key, values);
        } catch (Exception e) {
            log.error("移除Set集合中的值失败: {}", key, e);
            return 0L;
        }
    }

    // =============================== list 操作 =================================

    /**
     * 获取列表缓存的内容
     *
     * @param key   键
     * @param start 开始索引
     * @param end   结束索引 (0 到 -1 代表所有值)
     * @param <T>   列表元素的类型
     * @return 列表
     */
    public <T> List<T> listGet(String key, long start, long end) {
        try {
            List<Object> list = redisTemplate.opsForList().range(key, start, end);
            if (list == null) {
                return List.of();
            }
            return (List<T>) list;
        } catch (Exception e) {
            log.error("获取列表失败: {}", key, e);
            return List.of();
        }
    }

    /**
     * 获取列表缓存的长度
     *
     * @param key 键
     * @return 列表长度
     */
    public long listGetSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("获取列表大小失败: {}", key, e);
            return 0L;
        }
    }

    /**
     * 通过索引获取列表中的值
     *
     * @param key   键
     * @param index 索引 (index >= 0时，0 是表头，1 是第二个元素，以此类推；
     *              index < 0时，-1 是表尾，-2 是倒数第二个元素，以此类推)
     * @return 值
     */
    public <T> T listGetIndex(String key, long index) {
        try {
            Object obj = redisTemplate.opsForList().index(key, index);
            if (obj == null) {
                return null;
            }
            return (T) obj;
        } catch (Exception e) {
            log.error("获取列表中指定索引的值失败: key={}, index={}", key, index, e);
            return null;
        }
    }

    /**
     * 将数据放入列表（左侧入队）
     *
     * @param key   键
     * @param value 值
     * @return true 成功 false 失败
     */
    public boolean leftPush(String key, Object value) {
        return leftPush(key, value, 0L);
    }

    /**
     * 将数据放入列表（左侧入队）
     *
     * @param key   键
     * @param value 值
     * @param time  过期时间（秒），0或负数代表永久有效
     * @return true 成功 false 失败
     */
    public boolean leftPush(String key, Object value, long time) {
        return leftPush(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 将数据放入列表（左侧入队）
     *
     * @param key      键
     * @param value    值
     * @param time     过期时间，0或负数代表永久有效
     * @param timeUnit 时间单位
     * @return true 成功 false 失败
     */
    public boolean leftPush(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            if (time > 0) {
                expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key    键
     * @param values 值列表
     * @return true 成功 false 失败
     */
    public boolean leftPushAll(String key, List<Object> values) {
        return leftPushAll(key, values, 0L);
    }

    /**
     * 将列表数据放入缓存（左侧批量入队）
     *
     * @param key    键
     * @param values 值列表
     * @param time   过期时间（秒），0或负数代表永久有效
     * @return true 成功 false 失败
     */
    public boolean leftPushAll(String key, List<Object> values, long time) {
        return leftPushAll(key, values, time, TimeUnit.SECONDS);
    }

    /**
     * 将列表数据放入缓存（左侧批量入队）
     *
     * @param key      键
     * @param values   值列表
     * @param time     过期时间，0或负数代表永久有效
     * @param timeUnit 时间单位
     * @return true 成功 false 失败
     */
    public boolean leftPushAll(String key, List<Object> values, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForList().leftPushAll(key, values);
            if (time > 0) {
                expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error("设置列表值失败: {}", key, e);
            return false;
        }
    }

    /**
     * 将数据放入列表（右侧入队）
     *
     * @param key   键
     * @param value 值
     * @return true 成功 false 失败
     */
    public boolean rightPush(String key, Object value) {
        return rightPush(key, value, 0L);
    }

    /**
     * 将数据放入列表（右侧入队）
     *
     * @param key   键
     * @param value 值
     * @param time  过期时间（秒），0或负数代表永久有效
     * @return true 成功 false 失败
     */
    public boolean rightPush(String key, Object value, long time) {
        return rightPush(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * 将数据放入列表（右侧入队）
     *
     * @param key      键
     * @param value    值
     * @param time     过期时间，0或负数代表永久有效
     * @param timeUnit 时间单位
     * @return true 成功 false 失败
     */
    public boolean rightPush(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key    键
     * @param values 值列表
     * @return true 成功 false 失败
     */
    public boolean rightPushAll(String key, List<Object> values) {
        return rightPushAll(key, values, 0L);
    }

    /**
     * 将列表数据放入缓存（右侧批量入队）
     *
     * @param key    键
     * @param values 值列表
     * @param time   过期时间（秒），0或负数代表永久有效
     * @return true 成功 false 失败
     */
    public boolean rightPushAll(String key, List<Object> values, long time) {
        return rightPushAll(key, values, time, TimeUnit.SECONDS);
    }

    /**
     * 将列表数据放入缓存（右侧批量入队）
     *
     * @param key    键
     * @param values 值列表
     * @param time   过期时间（秒），0或负数代表永久有效
     * @return true 成功 false 失败
     */
    public boolean rightPushAll(String key, List<Object> values, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForList().rightPushAll(key, values);
            if (time > 0) {
                expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error("设置列表值失败: {}", key, e);
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return /
     */
    public boolean listUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 移除 N 个值为 value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long listRemove(String key, long count, Object value) {
        try {
            return redisTemplate.opsForList().remove(key, count, value);
        } catch (Exception e) {
            log.error("移除列表中指定值失败: {}", key, e);
            return 0L;
        }
    }

    // --- 发布/订阅(Pub/Sub)操作 ---

    /**
     * 发送对象消息到指定频道
     *
     * @param channel 主题
     * @param message 消息对象
     * @param <T>     消息的类型
     */
    public <T> void sendObjectMessage(String channel, T message) {
        try {
            redisTemplate.convertAndSend(channel, message);
            log.debug("发送对象消息成功: channel:{}, msg:{} ", channel, message);
        } catch (Exception e) {
            log.error("发送对象消息失败: channel:{}, msg:{} ", channel, message, e);
        }
    }

    /**
     * 发送消息（使用字符串模板）
     */
    public void sendStringMsg(String channel, String msg) {
        stringRedisTemplate.convertAndSend(channel, msg);
        log.debug("发送消息成功: channel:{}, msg:{} ", channel, msg);
    }

}
