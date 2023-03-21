package fa.training.frontend.controller;

import model.Course;
import model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${courses.api.url}")
    private String apiUrl;

    @GetMapping("teacher/{id}")
    public String userProfile(Model model, @PathVariable("id") int id) {
        String url = apiUrl + "/user/teacher/" + id;
        User teacher = restTemplate.getForObject(url, User.class);
        model.addAttribute("teacher", teacher);
        return "user-profile";
    }


}
