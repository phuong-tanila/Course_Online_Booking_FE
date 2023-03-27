package fa.training.frontend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fa.training.frontend.helpers.jwt.JwtClaims;
import fa.training.frontend.helpers.jwt.JwtProvider;
import fa.training.frontend.model.Course;
import fa.training.frontend.model.Order;
import fa.training.frontend.model.TokenAuthModel;
import fa.training.frontend.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.security.core.Authentication;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${courses.api.url}")
    private String apiUrl;

    @GetMapping("teacher/{id}")
    public String teacherProfile(Model model, @PathVariable("id") int id) {
        String url = apiUrl + "/user/teacher/" + id;
        User teacher = restTemplate.getForObject(url, User.class);
        model.addAttribute("teacher", teacher);
        return "teacher-profile";
    }

    @GetMapping("/profile")
    public String userProfile(Model model, HttpServletRequest request, HttpServletResponse response) throws JsonProcessingException {

        Cookie[] cookies = request.getCookies();
        boolean foundTokenCookie = false;
        String accessToken = null;
        JwtClaims claims = null;
        if (cookies != null) {
            TokenAuthModel tokenAuthModel = new TokenAuthModel();
            for (Cookie cooky : cookies) {
                System.out.println(cooky.getName());
                if (cooky.getName().equals("accessToken")) {
                    foundTokenCookie = true;
                    tokenAuthModel.setAccessToken(cooky.getValue());
                    accessToken = cooky.getValue();
                } else if (cooky.getName().equals("refreshToken")) {
                    foundTokenCookie = true;
                    tokenAuthModel.setRefreshToken(cooky.getValue());

                }
            }
            if (tokenAuthModel.getRefreshToken() != null && tokenAuthModel.getAccessToken() != null) {
                try {
                    accessToken = tokenAuthModel.getAccessToken();
                    JwtProvider.validateAccessToken(accessToken);
                    claims = JwtProvider.getClaimsFromJWT(accessToken);

                } catch (ExpiredJwtException ex) {
                    tokenAuthModel = JwtProvider.refreshNewToken(
                            tokenAuthModel,
                            apiUrl,
                            restTemplate,
                            response
                    );
                    accessToken = tokenAuthModel.getAccessToken();
                }
            }
        }
        System.out.println(accessToken);
        if (accessToken != null) {

            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);
            HttpEntity<String> httpEntity = new HttpEntity<>(headers);
            String url = apiUrl + "/user/profile-info";
            User user;
            try {
                ResponseEntity<User> res = restTemplate.exchange(
                        url, HttpMethod.GET,
                        httpEntity, User.class,
                        new Object());
                user = res.getBody();
            } catch (Exception ex) {
                ex.printStackTrace();
                user = new User();
            }
            model.addAttribute("user", user);
            model.addAttribute("logout", false);
            System.out.println(false);
        }else{
            System.out.println(true);
            model.addAttribute("logout", true);
        }
        return "user-profile";
    }


}
