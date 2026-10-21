package com.dongh.admin.service;

/**
 * Redis cache service (introduced in week 5).
 *
 * Wraps common cache operations; buyers can inject and use it in any business logic.
 */
public interface RedisService {

    /** Write to cache with an expiration time (seconds) */
    void set(String key, Object value, long timeoutSeconds);

    /** Read from cache and deserialize to the given type; returns null if absent */
    <T> T get(String key, Class<T> clazz);

    /** Delete a cache entry */
    boolean delete(String key);

    /** Check whether a key exists */
    boolean hasKey(String key);
}
