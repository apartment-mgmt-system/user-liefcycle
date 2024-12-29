package com.apartmentServiceMgmt.UserLifecycleManagement.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.apartmentServiceMgmt.UserLifecycleManagement.entity.UserEntity;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTRequestFilter extends OncePerRequestFilter {

    private static final String ROLES = "ROLES";
    private static final String EXPIRED_JWT_MSG = "JWT is Expired!!!";
    private static final String MISSING_JWT = "No JWT in cookie";

    @Autowired
    private JWTUtil jwtUtil;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestUri = request.getRequestURI();
        log.info("Processing JWTRequestFilter: {}", requestUri);

        if (isPermittedUrl(requestUri)) {
            log.info("This is permitted URL, hence skipping JWT validation: {}", requestUri);
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = jwtUtil.extractJwtFromCookies(request.getCookies());
        if (Objects.isNull(jwtToken)) {
            log.error(MISSING_JWT);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, MISSING_JWT);
            return;
        }

        if (jwtUtil.isTokenExpired(jwtToken)) {
            log.error(EXPIRED_JWT_MSG);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, EXPIRED_JWT_MSG);
            return;
        }

        log.info("Token : {} extracted, validating details", jwtToken);
        validateTokenDetails(request, jwtToken);
        filterChain.doFilter(request, response);
    }

    private boolean isPermittedUrl(String requestUri) {
        return requestUri.startsWith("/swagger-ui") || 
               requestUri.startsWith("/v3/api-docs/") || 
               requestUri.equals("/UserAndActorManagement.yml") ||
               requestUri.equals("/LoginMgmt.yml") || 
               requestUri.equals("/auth/login") || 
               requestUri.endsWith("/UserAndActorManagement.yml") ||
               requestUri.endsWith("/LoginMgmt.yml") /*||
               requestUri.startsWith("/admin/visitors")*/;
    }

    
    private void validateTokenDetails(HttpServletRequest request, String jwtToken) {
        String userName;
        Integer userId;

        UserEntity userInfo = jwtUtil.extractUserDetails(jwtToken);
        userName = userInfo.getUserName();
        userId = userInfo.getUserId().intValue();
        log.info("Extracted user details, name: {} & userMasterId: {}", userName, userId);

        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            log.info("Initiating Authenticate for User-name: {}", userName);
            authenticateUser(request, userName, userId, jwtToken);
        } else {
            log.warn("No userName found or user already authenticated");
        }
    }

    private void authenticateUser(HttpServletRequest request, String username, Integer userMasterId, String jwtToken) {
        UserEntity userInfo = new UserEntity();
        userInfo.setUserName(username);
        userInfo.setUserId(userMasterId.longValue());

        Claims claims = jwtUtil.extractAllClaims(jwtToken);
        List<String> roles = (List<String>) claims.get(ROLES);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userInfo, null,
                authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
        log.info("Created Auth token and set into security context for user-name: {}", username);
    }
}
