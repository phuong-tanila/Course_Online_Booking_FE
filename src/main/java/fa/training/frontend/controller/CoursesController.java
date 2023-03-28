package fa.training.frontend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import fa.training.frontend.helpers.jwt.JwtClaims;
import fa.training.frontend.helpers.jwt.JwtProvider;
import fa.training.frontend.model.Category;
import fa.training.frontend.model.Course;
import fa.training.frontend.model.TokenAuthModel;
import fa.training.frontend.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        String url1 = apiUrl + "/courses/slider/createDate";
        List<Course> courses1 = List.of(restTemplate.getForObject(url1, Course[].class));
        model.addAttribute("courses1", courses1);
        String url2 = apiUrl + "/categories/";
        List<Category> categories = List.of(restTemplate.getForObject(url2, Category[].class));
        HttpSession session = request.getSession();
        session.setAttribute("categories", categories);
        String url3 = apiUrl + "/user/list-teacher";
        List<User> teachers = List.of(restTemplate.getForObject(url3, User[].class));
        session.setAttribute("teachers", teachers);
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
    ) throws JsonProcessingException {
        String url = apiUrl + "/courses/" + courseId;
        Course course = restTemplate.getForObject(url, Course.class);
        System.out.println(course);
        model.addAttribute("course", course);
        String url1 = apiUrl + "/courses/slider/soldCount";
        List<Course> courses1 = List.of(restTemplate.getForObject(url1, Course[].class));
        model.addAttribute("courses1", courses1);
        Cookie[] cookies = request.getCookies();
        boolean foundTokenCookie = false;
        String accessToken = null;
        JwtClaims claims = null;
        String role = "US";
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
            if(tokenAuthModel.getRefreshToken() != null && tokenAuthModel.getAccessToken() != null){
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
                    System.out.println(tokenAuthModel);

                }finally{
                    if(claims != null) role = claims.role;

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
        sortBy = sortBy.split(",")[0];
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
        model.addAttribute("paginationBy", "slider");

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

    @GetMapping("search/{sortBy}")
    public String search(Model model, @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "1") int pageNo,
            @PathVariable("sortBy") String sortBy) {
        int pageSize = 20;
        List<Course> courses = new ArrayList<>();
        if (!name.trim().isEmpty()) {
            String url = apiUrl + "/courses/total-course-search?name=" + name;
            int pageNum = 20;
            int totalCourse = restTemplate.getForObject(url, Integer.class);
            int totalPage = totalCourse % pageNum == 0 ? totalCourse / pageNum : totalCourse / pageNum + 1;
            if (totalPage >= pageNo) {
                url = apiUrl + "/courses/search?name=" + name + "&sort=" + sortBy + "&pageNo=" + (pageNo - 1) + "&pageSize=" + pageSize;
                courses = List.of(restTemplate.getForObject(url, Course[].class));
                model.addAttribute("pageNo", pageNo);
                model.addAttribute("paginationBy", "search");
                model.addAttribute("name", name);
                if (totalPage > 0) {
                    List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPage)
                            .boxed()
                            .collect(Collectors.toList());
                    model.addAttribute("pageNumbers", pageNumbers);
                    model.addAttribute("totalPage", totalPage);
                }
            }

        }
        model.addAttribute("courses", courses);
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
