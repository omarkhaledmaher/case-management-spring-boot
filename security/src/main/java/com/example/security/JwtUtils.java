package com.example.security;

import java.security.Key;
import java.util.Date;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.example.common.dto.JwtDto;
import com.example.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    private static final Logger logger = LogManager.getLogger(JwtUtils.class);

    public JwtDto generateJwtToken(User user) {
        return new JwtDto(
                Jwts.builder()
                        .setSubject((user.getUsername()))
                        .setIssuedAt(new Date())
                        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                        .signWith(key(), SignatureAlgorithm.HS256)
                        .compact(),
                jwtExpirationMs);
    }

    public JwtDto generateJwtToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return new JwtDto(
                Jwts.builder()
                        .setSubject((userDetails.getUsername()))
                        .setIssuedAt(new Date())
                        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                        .signWith(key(), SignatureAlgorithm.HS256)
                        .compact(),
                jwtExpirationMs);
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
