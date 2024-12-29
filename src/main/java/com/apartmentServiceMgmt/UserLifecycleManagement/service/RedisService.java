package com.apartmentServiceMgmt.UserLifecycleManagement.service;

public interface RedisService {
	void storeExpiredToken(String token, long ttl);
}
