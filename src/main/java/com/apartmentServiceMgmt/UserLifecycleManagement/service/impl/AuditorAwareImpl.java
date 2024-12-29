package com.apartmentServiceMgmt.UserLifecycleManagement.service.impl;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.apartmentServiceMgmt.UserLifecycleManagement.entity.UserEntity;

import lombok.extern.slf4j.Slf4j;

@Component("auditorAware")
@Slf4j
public class AuditorAwareImpl implements AuditorAware<String> {

	@Override
	public Optional<String> getCurrentAuditor() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return Optional.of("system");
		} else {
			Object principal = authentication.getPrincipal();

			if (principal instanceof String username) {
				log.info("Principal is a String: " + username);
				return Optional.of(username);
			} else if (principal instanceof UserEntity userEntity) {
				log.info("Principal is a UserEntity: " + userEntity.getUserName());
				return Optional.ofNullable(userEntity.getUserName());
			} else {
				log.info("Unknown principal type: " + principal.getClass().getName());
				return Optional.empty();
			}
		}

	}
}
