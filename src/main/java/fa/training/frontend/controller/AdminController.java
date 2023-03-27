package fa.training.frontend.controller;

import fa.training.frontend.model.Category;
import fa.training.frontend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class AdminController {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${courses.api.url}")
    private String apiUrl;

    @GetMapping("/admin-create")
    public String adminCreate(){
        return "admin-create-course";
    }

    @GetMapping("/admin-update")
    public String adminUpdate(){
        return "admin-update-course";
    }

}
