package fa.training.frontend.controller;

import fa.training.frontend.helpers.JwtProvider;
import io.jsonwebtoken.Claims;
import model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;

@Controller
public class CoursesController {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${courses.api.url}")
    private String apiUrl;

    @GetMapping("/home")
    public String home(Model model) {
        String url = apiUrl + "/courses/slider-popular";
        List<Course> courses = List.of(restTemplate.getForObject(url, Course[].class));
        model.addAttribute("courses", courses);
//        restTemplate.
        String url1 = apiUrl + "/courses/slider-newest";
        List<Course> courses1 = List.of(restTemplate.getForObject(url1, Course[].class));
        model.addAttribute("courses1", courses1);
        return "home-page";
    }

    @GetMapping("/")
    public String index(Model model) {
        String url = apiUrl + "/courses";
        List<Course> courses = List.of(restTemplate.getForObject(url, Course[].class));
        model.addAttribute("courses", courses);

        return "home-page";
    }

    @GetMapping("/course-detail/{courseId}")
    public String getCourseDetail(
            @PathVariable("courseId") int courseId,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        String url = apiUrl + "/courses/" + courseId;
        Course course = restTemplate.getForObject(url, Course.class);
        model.addAttribute("course", course);
        String url1 = apiUrl + "/courses/slider-newest";
        List<Course> courses1 = List.of(restTemplate.getForObject(url1, Course[].class));
        model.addAttribute("courses1", courses1);

        Cookie[] cookies = request.getCookies();
        boolean foundTokenCookie = false;
        String role = "US";
        for (Cookie cooky : cookies) {
            if (cooky.getName().equals("accessToken")) {
                foundTokenCookie = true;
                try {
                    String accessToken = cooky.getValue();
                    JwtProvider.validateAccessToken(accessToken);
                    Claims claims = JwtProvider.getClaimsFromAccessToken(accessToken);
                    role = claims.get("role").toString();
                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    cooky.setMaxAge(0);
                }
            }
        }
        model.addAttribute("role", role);
        System.out.println(role);
        return "course-detail";
    }
//    @PostMapping("/")
//    public String getWeatherInfo(@ModelAttribute("weather") WeatherInfo weather, Model model) {
//        String url = apiUrl + "?city=" + weather.getName();
//        WeatherInfo weatherInfo = restTemplate.getForObject(url, WeatherInfo.class);
//        model.addAttribute("weather", weatherInfo);
//        return "weather";
//    }
}
