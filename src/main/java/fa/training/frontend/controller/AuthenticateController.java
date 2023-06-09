/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fa.training.frontend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import fa.training.frontend.model.*;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class AuthenticateController {

    //    @Autowired
//    PasswordEncoder passwordEncoder;
    @Autowired
    private RestTemplate restTemplate;
    @Value("${courses.api.url}")
    private String apiUrl;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/login")
    @ResponseBody
    void login(@RequestBody LoginRequestModel loginRequest, HttpServletResponse response) throws JsonProcessingException, IOException {
        String endpoint = apiUrl + "/auth/login";
        TokenAuthModel tokenModel = restTemplate.postForObject(endpoint, loginRequest, TokenAuthModel.class);
        System.out.println(tokenModel);
        System.out.println(loginRequest);
        response.setContentType("application/json");
        if (tokenModel.getAccessToken() != null) {
            Cookie refreshTokenCookie = new Cookie("refreshToken", tokenModel.getRefreshToken());
            Cookie accessTokenCookie = new Cookie("accessToken", tokenModel.getAccessToken());
            refreshTokenCookie.setMaxAge(60*60*24*24);
            accessTokenCookie.setMaxAge(60*60*24*24);
            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().print(objectMapper.writeValueAsString(tokenModel));
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            ExceptionResponse res = new ExceptionResponse(
                    "bad credentials!",
                    "invalid email/phone or password");
            response.getWriter().print(objectMapper.writeValueAsString(res));
        }
    }
    @GetMapping("/logout")
    @ResponseBody
    ResponseEntity logout(HttpServletResponse response){
        Cookie refreshCookie = new Cookie("refreshToken", "");
        refreshCookie.setMaxAge(0);
        Cookie accessCookie = new Cookie("accessToken", "");
        refreshCookie.setMaxAge(0);
        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
        return new ResponseEntity(HttpStatus.OK);
    }
    
    
    @PostMapping("/login/google")
    @ResponseBody
    void loginByGoogle(@RequestBody LoginRequestModel loginRequest, HttpServletResponse response) throws IOException{
        String endpoint = apiUrl + "/auth/login/google";
        TokenAuthModel tokenModel = restTemplate.postForObject(endpoint, loginRequest, TokenAuthModel.class);
        System.out.println("Token: " + tokenModel);
        System.out.println(loginRequest);
        response.setContentType("application/json");
        
        if (tokenModel.getAccessToken() != null) {
            Cookie refreshTokenCookie = new Cookie("refreshToken", tokenModel.getRefreshToken());
            Cookie accessTokenCookie = new Cookie("accessToken", tokenModel.getAccessToken());
            refreshTokenCookie.setMaxAge(60*60*24);
            accessTokenCookie.setMaxAge(60*60*24);
            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);
            response.setStatus(HttpStatus.OK.value());
            response.getWriter().print(objectMapper.writeValueAsString(tokenModel));
        }else{
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            ExceptionResponse res = new ExceptionResponse(
                    "bad credentials!", 
                    "Your email has not been registered in our system");
            response.getWriter().print(objectMapper.writeValueAsString(res));
        }
    }

    @GetMapping("/register-form")
    public String getForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public Object register(@ModelAttribute("user") User user, Model model) {
        String url = apiUrl + "/auth/register";
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<User> response = restTemplate.postForEntity(url, user, User.class);
            return new RedirectView("http://localhost:8083/home");
        } catch (Exception ex) {
            model.addAttribute("msg", "Email or phone number already exists");
            return "register";
        }
    }
}
