package com.apartmentServiceMgmt.UserLifecycleManagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class RedirectController {
	@GetMapping("/")
	public RedirectView redirectToSwagger() {
		return new RedirectView("/swagger-ui/index.html");
		}
}
