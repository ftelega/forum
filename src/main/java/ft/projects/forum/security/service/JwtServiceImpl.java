package ft.projects.forum.security.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class JwtServiceImpl implements JwtService {

    private static final List<String> INVALIDATED = new ArrayList<>();
    private static final ThreadLocal<String> JWT = new ThreadLocal<>();
    @Value("${signing-key}")
    private String signingKey;
    @Value("${token-expiration}")
    private Long tokenExpiration;

    @Override
    public String getToken(String username) {
        return Jwts.builder()
                .subject(username)
                .expiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    @Override
    public String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Override
    public void invalidate() {
        INVALIDATED.add(JWT.get());
    }

    @Override
    public boolean isInvalidated(String jwt) {
        return INVALIDATED.stream()
                .anyMatch(p -> p.equals(jwt));
    }

    @Override
    public void setToken(String jwt) {
        JWT.set(jwt);
    }

    @Override
    public void clearToken() {
        JWT.remove();
    }
}
