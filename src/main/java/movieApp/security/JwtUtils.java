package movieApp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class JwtUtils {

    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.expiration:1440}")
    private int jwtExpirationMinutes;

    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(String username) {
        log.info("Generating token for user: {}", username);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() +
                        TimeUnit.MINUTES.toMillis(jwtExpirationMinutes)))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public boolean validateToken(String token) {
        log.info("Validating token");

        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token);

            log.info("Token is valid");
            return true;

        } catch (ExpiredJwtException e) {
            log.warn("Token expired: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();

        } catch (ExpiredJwtException e) {
            log.warn("Token expired but extracting username");
            return e.getClaims().getSubject();
        } catch (Exception e) {
            log.error("Failed to get username: {}", e.getMessage());
            return null;
        }
    }
}