package fa.training.frontend.controller;

<<<<<<< HEAD
import fa.training.frontend.helpers.JwtProvider;
import io.jsonwebtoken.Claims;
=======
import model.Category;
>>>>>>> ed1df2d5b9231ee5b2d66cdf798de2cf023632ea
import model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
<<<<<<< HEAD
import java.util.List;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.CookieValue;
=======

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

>>>>>>> ed1df2d5b9231ee5b2d66cdf798de2cf023632ea

@Controller
public class CoursesController {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${courses.api.url}")
    private String apiUrl;

    @GetMapping("/home")
    public String home(Model model, HttpServletRequest request) {
        String url = apiUrl + "/courses/slider/soldCount";
        List<Course> courses = List.of(restTemplate.getForObject(url, Course[].class));
        model.addAttribute("courses", courses);
<<<<<<< HEAD
//        restTemplate.
        String url1 = apiUrl + "/courses/slider-newest";
=======
        String url1 = apiUrl + "/courses/slider/createDate";
>>>>>>> ed1df2d5b9231ee5b2d66cdf798de2cf023632ea
        List<Course> courses1 = List.of(restTemplate.getForObject(url1, Course[].class));
        model.addAttribute("courses1", courses1);
        String url2 = apiUrl + "/categories/";
        List<Category> categories = List.of(restTemplate.getForObject(url2, Category[].class));
        HttpSession session = request.getSession();
        session.setAttribute("categories", categories);
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
        String url1 = apiUrl + "/courses/slider/soldCount";
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

    @GetMapping("/courses-sortBy/{sortBy}")
    public String getCoursesList(Model model,
                                 @RequestParam(defaultValue = "1") int pageNo,
                                 @PathVariable("sortBy") String sortBy) {
        String url = apiUrl + "/courses/list/" + sortBy + "?pageNo=" + (pageNo - 1);
        List<Course> courses;
        try {
            courses = List.of(restTemplate.getForObject(url, Course[].class));
        } catch (Exception ex) {
            courses = new ArrayList<>();
        }
        model.addAttribute("courses", courses);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortByCourse", true);
        url = apiUrl + "/courses/total-course";
        int pageNum = 20;
        int totalCourse = restTemplate.getForObject(url, Integer.class);
        int totalPage = totalCourse % pageNum == 0 ? totalCourse / pageNum : totalCourse / pageNum + 1;
        if (totalPage > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPage)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
            model.addAttribute("totalPage", totalPage);
        }
        return "show-list-course";
    }

    @GetMapping("/contact-us")
    public String contact() {
        return "contact-us";
    }

//    @PostMapping("/")
//    public String getWeatherInfo(@ModelAttribute("weather") WeatherInfo weather, Model model) {
//        String url = apiUrl + "?city=" + weather.getName();
//        WeatherInfo weatherInfo = restTemplate.getForObject(url, WeatherInfo.class);
//        model.addAttribute("weather", weatherInfo);
//        return "weather";
//    }
}
