package com.dongh.admin.service;

/**
 * Redis 缓存服务（第 5 周接入）
 *
 * 封装常用缓存操作，买家可在任意业务中注入使用。
 */
public interface RedisService {

    /** 写入缓存，带过期时间（秒） */
    void set(String key, Object value, long timeoutSeconds);

    /** 读取缓存并反序列化为指定类型，不存在返回 null */
    <T> T get(String key, Class<T> clazz);

    /** 删除缓存 */
    boolean delete(String key);

    /** 判断 key 是否存在 */
    boolean hasKey(String key);
}
