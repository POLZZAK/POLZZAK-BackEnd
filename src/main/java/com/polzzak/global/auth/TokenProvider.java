package com.polzzak.global.auth;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.crypto.SecretKey;

import org.apache.http.HttpHeaders;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import com.polzzak.global.auth.properties.JwtProperties;
import com.polzzak.global.exception.ErrorCode;
import com.polzzak.global.exception.JwtException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class TokenProvider {

	private final JwtProperties jwtProperties;

	private final SecretKey key;

	public TokenProvider(final JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
		try {
			this.key = Keys.hmacShaKeyFor(jwtProperties.getKey().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new JwtException(ErrorCode.ACCESS_TOKEN_INVALID);
		}
	}

	public String createAccessToken(final String payload) {
		return createToken(payload, jwtProperties.getExpiredTimeMs());
	}

	public String createRefreshToken(final String payload) {
		return createToken(payload, jwtProperties.getRefreshExpiredTimeMs());
	}

	public boolean isValidToken(final String token) {
		try {
			getClaimsJws(token);
			return true;
		} catch (ExpiredJwtException e) {
			throw new JwtException(ErrorCode.ACCESS_TOKEN_INVALID);
		} catch (Exception e) {
			return false;
		}
	}

	public String getSubject(final String token) {
		return getClaimsJws(token)
			.getBody()
			.getSubject();
	}

	public String extractAccessToken(final WebRequest request) {
		String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

		if (accessToken == null || accessToken.length() < 1) {
			throw new JwtException(ErrorCode.ACCESS_TOKEN_INVALID);
		}

		return extractToken(accessToken);
	}

	private String extractToken(final String accessToken) {
		Pair<String, String> tokenFormat = splitToTokenFormat(accessToken);

		String token = tokenFormat.getSecond();
		String tokenType = tokenFormat.getFirst();
		if (!tokenType.equals(jwtProperties.getType()) || !isValidToken(token)) {
			throw new JwtException(ErrorCode.ACCESS_TOKEN_INVALID);
		}

		return token;
	}

	private Pair<String, String> splitToTokenFormat(final String accessToken) {
		try {
			String[] tokenFormat = accessToken.split(" ");
			return Pair.of(tokenFormat[0], tokenFormat[1]);
		} catch (IndexOutOfBoundsException e) {
			throw new JwtException(ErrorCode.ACCESS_TOKEN_INVALID);
		}
	}

	private Jws<Claims> getClaimsJws(final String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token);
	}

	private String createToken(final String payload, final long expiredTimeMs) {
		Claims claims = Jwts.claims().setSubject(payload);
		Date now = new Date(System.currentTimeMillis());
		Date expiration = new Date(now.getTime() + expiredTimeMs);

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)
			.setExpiration(expiration)
			.signWith(key)
			.compact();
	}
}
