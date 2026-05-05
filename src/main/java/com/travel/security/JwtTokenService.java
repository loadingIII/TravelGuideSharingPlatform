package com.travel.security;

import com.travel.common.exception.BusinessException;
import com.travel.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenService {
    private final JwtProperties jwtProperties;
    private SecretKey secretKey;

    @PostConstruct
    public void initSecretKey() {
        if (!StringUtils.hasText(jwtProperties.getSecret())) {
            throw new IllegalStateException("JWT secret must not be empty");
        }
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Long userId, String username) {
        Instant now = Instant.now();
        Instant expiredAt = now.plus(jwtProperties.getAccessExpireMinutes(), ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiredAt))
                .signWith(secretKey)
                .compact();
    }

    public LoginUser parseAccessToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            Long userId = Long.parseLong(claims.getSubject());
            String username = claims.get("username", String.class);
            return new LoginUser(userId, username);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "登录态已失效，请重新登录");
        }
    }

    public long getAccessExpireMinutes() {
        return jwtProperties.getAccessExpireMinutes();
    }
}
