package fa.training.frontend.controller;

import model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import javax.websocket.server.PathParam;
import java.util.List;


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
//    @PostMapping("/")
//    public String getWeatherInfo(@ModelAttribute("weather") WeatherInfo weather, Model model) {
//        String url = apiUrl + "?city=" + weather.getName();
//        WeatherInfo weatherInfo = restTemplate.getForObject(url, WeatherInfo.class);
//        model.addAttribute("weather", weatherInfo);
//        return "weather";
//    }
}
