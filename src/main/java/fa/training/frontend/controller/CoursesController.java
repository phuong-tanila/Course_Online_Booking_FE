package fa.training.frontend.controller;

import model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import javax.websocket.server.PathParam;
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
    public String home(Model model) {
        String url = apiUrl + "/courses/slider-popular";
        List<Course> courses = List.of(restTemplate.getForObject(url, Course[].class));
        model.addAttribute("courses", courses);
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
    public String getCourseDetail(@PathVariable("courseId") int courseId, Model model){
        String url = apiUrl  + "/courses/" + courseId;
        Course course = restTemplate.getForObject(url, Course.class);
        model.addAttribute("course", course);
        String url1 = apiUrl + "/courses/slider-newest";
        List<Course> courses1 = List.of(restTemplate.getForObject(url1, Course[].class));
        model.addAttribute("courses1", courses1);
        return "course-detail";
    }

    @GetMapping("/courses-newest")
    public String getCoursesNewest(Model model, @RequestParam(defaultValue = "1") int pageNo) {
        String url = apiUrl + "/courses/newest" + "?pageNo=" + (pageNo-1);
        List<Course> courses;
        try{
            courses = List.of(restTemplate.getForObject(url, Course[].class));
        }catch (Exception ex){
            courses = new ArrayList<>();
        }
        model.addAttribute("courses", courses);
        model.addAttribute("pageNo", pageNo);
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
//    @PostMapping("/")
//    public String getWeatherInfo(@ModelAttribute("weather") WeatherInfo weather, Model model) {
//        String url = apiUrl + "?city=" + weather.getName();
//        WeatherInfo weatherInfo = restTemplate.getForObject(url, WeatherInfo.class);
//        model.addAttribute("weather", weatherInfo);
//        return "weather";
//    }
}
