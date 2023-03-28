package fa.training.frontend.controller;

import fa.training.frontend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboradController {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${courses.api.url}")
    private String apiUrl;

    @GetMapping("/dashboard")
    public String dasboard(Model model){
        String url = apiUrl + "/user/list-user/";
        List<User> users;
        try {
            users = List.of(restTemplate.getForObject(url, User[].class));
        } catch (Exception ex) {
            users = new ArrayList<>();
        }
        model.addAttribute("users", users);
        return "admin-dashboard";
    }

    @GetMapping("/view-profile")
    public String viewProfile(Model model, @RequestParam int id, @RequestParam String role ){
        String url = apiUrl + "/user/" + id + "/" + role;
        User user = restTemplate.getForObject(url, User.class);
        model.addAttribute("user", user);
        return "view-profile";
    }
}
