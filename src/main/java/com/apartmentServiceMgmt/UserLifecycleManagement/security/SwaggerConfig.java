package com.apartmentServiceMgmt.UserLifecycleManagement.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI customOpenAPI() {
	    return new OpenAPI()
	            .info(new Info().title("JavaInUse Authentication Service"))
	            .addSecurityItem(new SecurityRequirement().addList("CookieSecurityScheme"))
	            .components(new Components()
	                    .addSecuritySchemes("CookieSecurityScheme", new SecurityScheme()
	                            .name("CookieSecurityScheme")
	                            .type(SecurityScheme.Type.APIKEY)
	                            .in(SecurityScheme.In.COOKIE) // Specify that the scheme is in the cookie
	                            .name("token")));
	}

}
