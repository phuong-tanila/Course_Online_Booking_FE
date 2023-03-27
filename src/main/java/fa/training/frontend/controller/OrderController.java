package fa.training.frontend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fa.training.frontend.helpers.jwt.JwtClaims;
import fa.training.frontend.helpers.jwt.JwtProvider;
import fa.training.frontend.model.Order;
import fa.training.frontend.model.TokenAuthModel;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OrderController {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${courses.api.url}")
    private String apiUrl;

    @GetMapping("/history")
    public String index(
            Model model,
            @RequestParam(defaultValue = "1") int pageNo,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws JsonProcessingException {
        System.out.println("1111");
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
            int userId = 3;
            String url = apiUrl + "/order/";// + "?pageNo=" + (pageNo - 1);
            List<Order> orders;
            try {
                ResponseEntity<Order[]> res = restTemplate.exchange(
                        url, HttpMethod.GET, 
                        httpEntity, Order[].class,
                        new Object());
                orders = List.of(res.getBody());
                System.out.println(res);
                System.out.println(orders);
            } catch (Exception ex) {
                ex.printStackTrace();
                orders = new ArrayList<>();
            }
            model.addAttribute("orders", orders);
            model.addAttribute("logout", false);
            System.out.println(false);
        }else{
            System.out.println(true);
            model.addAttribute("logout", true);
        }
//        model.addAttribute("pageNo", pageNo);
//        url = apiUrl + "/categories/total-course/" + id;
//        int pageNum = 20;
//        int totalCourse = restTemplate.getForObject(url, Integer.class);
//        int totalPage = totalCourse % pageNum == 0 ? totalCourse / pageNum : totalCourse / pageNum + 1;
//        if (totalPage > 0) {
//            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPage)
//                    .boxed()
//                    .collect(Collectors.toList());
//            model.addAttribute("pageNumbers", pageNumbers);
//            model.addAttribute("totalPage", totalPage);
//        }
        return "history-booking";
    }

    @GetMapping("/cart")
    String renderCartPage() {
        return "cart-checkout";
    }

    @PostMapping("/checkout")
    @ResponseBody
    ResponseEntity checkout(
            @RequestBody List<Integer> courseIds,
            @RequestParam(defaultValue = "") String paymentId,
            @RequestParam(defaultValue = "false") boolean paymentStatus,
            @RequestParam(defaultValue = "") String coupon,
            @RequestParam(defaultValue = "cod") String paymentMethod,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws JsonProcessingException {
        Cookie[] cookies = request.getCookies();
        boolean foundTokenCookie = false;
        String accessToken = null;
        JwtClaims claims = null;
        if (cookies != null) {
            TokenAuthModel tokenAuthModel = new TokenAuthModel();
            for (Cookie cooky : cookies) {
                if (cooky.getName().equals("accessToken")) {
                    foundTokenCookie = true;
                    tokenAuthModel.setAccessToken(cooky.getValue());
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
        if (accessToken != null) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);
            String url = apiUrl + "/order/checkout?"
                    + "paymentId=" + paymentId
                    + "&paymentStatus=" + paymentStatus
                    + "&paymentMethod=" + paymentMethod;
            Object[] uriVariables = new Object[2];
            HttpEntity<Object> entity = new HttpEntity<>(courseIds, headers);
            return restTemplate.exchange(
                    url, HttpMethod.POST,
                    entity, String.class,
                    uriVariables
            );
        } else {
            restTemplate.getForObject(apiUrl + "/logout", String.class);
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }
    }
}
