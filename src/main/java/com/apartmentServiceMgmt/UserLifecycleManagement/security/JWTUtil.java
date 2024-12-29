package com.apartmentServiceMgmt.UserLifecycleManagement.security;

import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.apartmentServiceMgmt.UserLifecycleManagement.entity.RoleEntity;
import com.apartmentServiceMgmt.UserLifecycleManagement.entity.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JWTUtil {

	private static final String USER_MASTER_ID = "USER_MASTER_ID";
	private static final String TOKEN = "token";
	private static final String ROLES = "ROLES";

	@Value("${jwt.expiration}")
	private long expirationTime;

	@Value("${jwt.secret.key}")
	private String secretKey;

	public String generateToken(UserEntity userInfo) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(USER_MASTER_ID, userInfo.getUserId());
		claims.put(ROLES, userInfo.getRoles().stream().map(RoleEntity::getRoleCode).collect(Collectors.toList()));
		return createToken(claims, userInfo.getUserName());
	}

	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().claims(claims).subject(subject).issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expirationTime)).signWith(getSignInKey()).compact();
	}

	private SecretKey getSignInKey() {
		/**
		 * One time key generation SecretKey sk = Jwts.SIG.HS256.key().build(); 
		 * byte[] keyBytes = sk.getEncoded();
		 * System.out.println(Base64.getEncoder().encodeToString(keyBytes));
		 */
		byte[] keyBytes = Base64.getDecoder().decode(secretKey);
		return new SecretKeySpec(keyBytes, "HmacSHA256");
	}

	public boolean isTokenExpired(String token) {
		try {
			return extractExpiration(token).before(new Date());
		} catch (ExpiredJwtException e) {
			return true;
		}
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getSignInKey())
				.build()
				.parseSignedClaims(token)				
				.getPayload();
	}

	public UserEntity extractUserDetails(String token) {
		Claims claims = extractAllClaims(token);
		log.info("Claims from JWT:",claims);
		UserEntity userDetails = new UserEntity();
		userDetails.setUserId(Integer.valueOf(claims.get(USER_MASTER_ID).toString()).longValue());
		userDetails.setUserName(claims.getSubject());
		return userDetails;
	}

	public int extractUserMasterIdFromJwt() {
		String jwtToken = extractUserJWT();
		return extractUserDetails(jwtToken).getUserId().intValue();
	}

	public String extractUsernameFromJwt() {
		String jwtToken = extractUserJWT();
		return extractUserDetails(jwtToken).getUserName();
	}

	private String extractUserJWT() {
		ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest request = attrs.getRequest();
		String jwtToken = extractJwtFromCookies(request.getCookies());
		return jwtToken;
	}

	public String extractJwtFromCookies(Cookie[] cookies) {
		if (cookies == null) {
			return null;
		}
		return Arrays.stream(cookies).filter(cookie -> TOKEN.equals(cookie.getName())).map(Cookie::getValue).findFirst()
				.orElse(null);
	}

}
