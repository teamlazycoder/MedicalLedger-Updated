package com.medical.demo.service.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    public String extractUsername(String token, SecretKey key) {
        return extractClaim(token, Claims::getSubject, key);
    }

    public Date extractExpiration(String token, SecretKey key) {
        return extractClaim(token, Claims::getExpiration, key);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, SecretKey key) {
        final Claims claims = extractAllClaims(token, key);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean isTokenExpired(String token, SecretKey key) {
        return extractExpiration(token, key).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails, SecretKey key) {
        final String username = extractUsername(token, key);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token, key));
    }
}
