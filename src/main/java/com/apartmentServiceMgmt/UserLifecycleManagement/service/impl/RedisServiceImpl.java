package com.apartmentServiceMgmt.UserLifecycleManagement.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.apartmentServiceMgmt.UserLifecycleManagement.service.RedisService;

@Service
public class RedisServiceImpl implements RedisService {

	@Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String EXPIRED_TOKENS_KEY = "expired-tokens";

    public void storeExpiredToken(String token, long ttl) {
        redisTemplate.opsForList().rightPush(EXPIRED_TOKENS_KEY, token);
        redisTemplate.expire(EXPIRED_TOKENS_KEY, ttl, TimeUnit.SECONDS);
    }

}
