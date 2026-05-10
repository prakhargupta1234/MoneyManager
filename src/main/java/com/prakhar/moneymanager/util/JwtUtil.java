package com.prakhar.moneymanager.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "mysecretkeymysecretkeymysecretkey"; // 32+ chars

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // ✅ Generate Token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Extract Username (SAFE)
    public String extractUsername(String token) {
        try {
            return getClaims(token).getSubject();
        } catch (ExpiredJwtException e) {
            System.out.println("❌ Token expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("❌ Unsupported JWT");
        } catch (MalformedJwtException e) {
            System.out.println("❌ Malformed JWT");
        } catch (SignatureException e) {
            System.out.println("❌ Invalid signature");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Token is null or empty");
        }
        return null;
    }

    // ✅ Validate Token (SAFE)
    public boolean validateToken(String token, String username) {
        try {
            String extractedUsername = extractUsername(token);

            return (extractedUsername != null &&
                    extractedUsername.equals(username) &&
                    !isTokenExpired(token));

        } catch (Exception e) {
            System.out.println("❌ Token validation failed: " + e.getMessage());
            return false;
        }
    }

    // ✅ Check Expiry (SAFE)
    private boolean isTokenExpired(String token) {
        try {
            return getClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    // ✅ Common method (BEST PRACTICE)
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .setAllowedClockSkewSeconds(30) // ⏱ small time diff allowed
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}