package com.apartmentServiceMgmt.UserLifecycleManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware", dateTimeProviderRef = "dateTimeProvider")
@EntityScan(basePackages = { "com.apartmentServiceMgmt.UserLifecycleManagement.entity" })
@EnableJpaRepositories(basePackages = { "com.apartmentServiceMgmt.UserLifecycleManagement.repository" })
@EnableMethodSecurity(securedEnabled = true)
public class UserLifecycleManagementApplication {

	public static void main(String[] args) {
		
		SpringApplication.run(UserLifecycleManagementApplication.class, args);
		
	}
	//TODO: integrate Redis via azure
	// update audit table in API mgmt & read only in other microservice
	
}
