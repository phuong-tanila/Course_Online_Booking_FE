package fa.training.frontend.controller;

import fa.training.frontend.model.Course;
import fa.training.frontend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
    public String teacherProfile(Model model, @PathVariable("id") int id) {
        String url = apiUrl + "/user/teacher/" + id;
        User teacher = restTemplate.getForObject(url, User.class);
        model.addAttribute("teacher", teacher);
        return "teacher-profile";
    }

    @GetMapping("/profile")
    public String userProfile(Model model) {
        String url = apiUrl + "/user/profile-info";
        User user = restTemplate.getForObject(url, User.class);
        System.err.println(user);
        model.addAttribute("user", user);
        return "user-profile";
    }
}
