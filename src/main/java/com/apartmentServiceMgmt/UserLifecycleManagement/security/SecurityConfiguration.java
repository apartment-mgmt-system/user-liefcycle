package com.apartmentServiceMgmt.UserLifecycleManagement.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
	private JWTRequestFilter jwtRequestFilter;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).sessionManagement(sess -> {
			sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		}).authorizeHttpRequests(auth -> {
			auth.requestMatchers(new AntPathRequestMatcher("/swagger-ui.html"),
					new AntPathRequestMatcher("/UserAndActorManagement.yml"),
					new AntPathRequestMatcher("/swagger-ui/**"), new AntPathRequestMatcher("/v3/api-docs/**"),
					new AntPathRequestMatcher("/auth/login"), new AntPathRequestMatcher("/UserAndActorManagement.yml"),
					new AntPathRequestMatcher("/LoginMgmt.yml")/*, new AntPathRequestMatcher("/admin/visitors/**")*/).permitAll().anyRequest().authenticated();
		}).addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// Stop random user creation
	@Bean
	UserDetailsService emptyDetailsService() {
		return username -> {
			throw new UsernameNotFoundException("No local users, only JWT tokens allowed");
		};
	}
}
