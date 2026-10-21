package com.dongh.admin.service.impl;

import com.dongh.admin.common.ResultCode;
import com.dongh.admin.common.exception.BusinessException;
import com.dongh.admin.service.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis 缓存服务实现
 *
 * 用 StringRedisTemplate 存 JSON 字符串，读写都走 ObjectMapper 序列化/反序列化，
 * 避免直接存对象带来的类型丢失问题。
 */
@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void set(String key, Object value, long timeoutSeconds) {
        try {
            String json = objectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, json, timeoutSeconds, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ResultCode.ERROR, "缓存序列化失败");
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ResultCode.ERROR, "缓存反序列化失败");
        }
    }

    @Override
    public boolean delete(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.delete(key));
    }

    @Override
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }
}
