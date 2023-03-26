package fa.training.frontend.helpers.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fa.training.frontend.model.TokenAuthModel;
import fa.training.frontend.model.User;
import io.jsonwebtoken.*;
import java.util.Base64;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.client.RestTemplate;

@AllArgsConstructor
@Getter
@Setter
//@ConfigurationProperties(prefix ="token")
class TokenInfo {

    public static String accessSecretKey = "UkXp2s5v8y/B?E(H+MbQeThWmYq3t6w9z$C&F)J@NcRfUjXn2r4u7x!A%D*G-KaP";
    public static Long accessExpiration = 300000L;
    public static String refreshSecretKey = "QeThWmZq4t7w!z%C&F)J@NcRfUjXn2r5u8x/A?D(G-KaPdSgVkYp3s6v9y$B&E)H";
    public static Long refreshExpiration = 604800000L;
}

@Slf4j
@Configuration
public class JwtProvider {

    public static String generateToken(User user, String jwtSecret, long jwtExpiration) {
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

    public static JwtClaims getClaimsFromJWT(String token) throws JsonProcessingException {
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String[] chunks = token.split("\\.");
        String payload = new String(decoder.decode(chunks[1]));
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(payload, JwtClaims.class);
    }

    public static String getUserEmailFromJWT(String token) throws JsonProcessingException {
        return getClaimsFromJWT(token).sub;
    }

    public static String getUserEmailFromJWT(String token, String jwtSecret) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
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
        Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
        return true;
    }

    public static TokenAuthModel refreshNewToken(
            TokenAuthModel tokenAuthModel, 
            String apiUrl, 
            RestTemplate restTemplate,
            HttpServletResponse response) {
        String url = apiUrl + "/auth/refresh";
        System.out.println(tokenAuthModel);
        tokenAuthModel = restTemplate.postForObject(url, tokenAuthModel, TokenAuthModel.class);
        System.out.println(tokenAuthModel);
        Cookie refreshCookie = new Cookie("refreshToken", tokenAuthModel.getRefreshToken());
        refreshCookie.setMaxAge(60 * 60 * 24);
        Cookie accessCookie = new Cookie("accessToken", tokenAuthModel.getAccessToken());
        refreshCookie.setMaxAge(60 * 60 * 24);
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
        return tokenAuthModel;
    }
}
