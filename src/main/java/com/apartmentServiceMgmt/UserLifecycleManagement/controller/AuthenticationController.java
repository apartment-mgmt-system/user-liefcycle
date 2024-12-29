package com.apartmentServiceMgmt.UserLifecycleManagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.apartmentServiceMgmt.UserLifecycleManagement.security.JWTUtil;
import com.apartmentServiceMgmt.UserLifecycleManagement.service.RedisService;
import com.apartmentServiceMgmt.UserLifecycleManagement.service.UserService;
import com.apartmentServiceMgmt.api.AuthenticationApi;
import com.apartmentServiceMgmt.model.LogoutResponse;
import com.apartmentServiceMgmt.model.SucessfulLoginResponse;
import com.apartmentServiceMgmt.model.UserLoginRequest;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
public class AuthenticationController implements AuthenticationApi {
	@Autowired
	UserService userService;
	@Autowired
    private JWTUtil jwtUtil;
	@Autowired
	private RedisService tokenRedisService;
	@Value("${jwt.expiration}")
	private long expirationTime;
	@Value("${test.property}")
	private String test;

	@Override
	public ResponseEntity<SucessfulLoginResponse> userLogin(@Valid UserLoginRequest userLoginRequest) {
        SucessfulLoginResponse loginResponse = userService.authenticateUser(userLoginRequest);
        Cookie cookie = new Cookie("token", loginResponse.getToken());
        cookie.setHttpOnly(true); 
        cookie.setPath("/");

        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        if (response != null) {
            response.addCookie(cookie);
        }

		return ResponseEntity.ok(loginResponse);
	}
	
    @Override
    public ResponseEntity<LogoutResponse> userLogout() {
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        if (response != null) {
            Cookie cookie = new Cookie("token", null);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);

            // Token Blacklisting
            String jwtToken = jwtUtil.extractJwtFromCookies(request.getCookies());
            if (jwtToken != null) {
                tokenRedisService.storeExpiredToken(jwtToken, expirationTime);
            }
        }
        LogoutResponse logoutResponse = new LogoutResponse();
        logoutResponse.setMessage("User logged out successfully");
        return ResponseEntity.ok(logoutResponse);
    }

   
}
