package com.hadoken.common.core;


import lombok.Data;

/**
 * Key Value 的键值对
 *
 * @author yanggj
 * @version 1.0.0
 * @date 2022/02/28 15:53
 */
@Data
public class KeyValue<K, V> {

    private K key;
    private V value;

    public KeyValue() {
    }

    public KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
