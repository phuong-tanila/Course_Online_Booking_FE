package fa.training.frontend.helpers;

import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import model.User;

@AllArgsConstructor
@Getter
@Setter
//@ConfigurationProperties(prefix ="token")

@Slf4j
@Configuration
public class JwtProvider {

    private class TokenInfo {

        private static String accessSecretKey = "UkXp2s5v8y/B?E(H+MbQeThWmYq3t6w9z$C&F)J@NcRfUjXn2r4u7x!A%D*G-KaP";
        private static Long accessExpiration = 300000L;
        private static String refreshSecretKey = "QeThWmZq4t7w!z%C&F)J@NcRfUjXn2r5u8x/A?D(G-KaPdSgVkYp3s6v9y$B&E)H";
        private static Long refreshExpiration = 604800000L;
    }

    private static String generateToken(User user, String jwtSecret, long jwtExpiration) {
        System.out.println(jwtSecret);
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("fullname", user.getFullname());
        claims.put("avatar", user.getAvatar());
        // Tạo chuỗi json web token từ id của user.
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public static String generateRefreshToken(User user) {
        return generateToken(user, TokenInfo.refreshSecretKey, TokenInfo.refreshExpiration);
    }

    public static String generateAccessToken(User user) {
        return generateToken(user, TokenInfo.accessSecretKey, TokenInfo.accessExpiration);
    }

    private static Claims getClaims(String token, String jwtSecret) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    public static Claims getClaimsFromAccessToken(String token) {
        return getClaims(token, TokenInfo.accessSecretKey);
    }

    public static Claims getClaimsFromRefreshToken(String token) {
        return getClaims(token, TokenInfo.refreshSecretKey);
    }

    private static String getUserEmailFromJWT(String token, String jwtSecret) {
        return getClaims(token, jwtSecret).getSubject();
    }

    public static String getUserEmailFromAccessToken(String token) {
        return getUserEmailFromJWT(token, TokenInfo.accessSecretKey);
    }

    public static String getUserEmailFromRefreshToken(String token) {
        return getUserEmailFromJWT(token, TokenInfo.refreshSecretKey);
    }

    public static boolean validateRefreshToken(String token) {
        return validateToken(token, TokenInfo.refreshSecretKey);
    }

    public static boolean validateAccessToken(String token) {
        return validateToken(token, TokenInfo.accessSecretKey);
    }

    public static boolean validateToken(String authToken, String jwtSecret) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }
}
