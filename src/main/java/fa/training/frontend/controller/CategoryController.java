package fa.training.frontend.controller;

import fa.training.frontend.model.Category;
import fa.training.frontend.model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Controller
public class CategoryController {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${courses.api.url}")
    private String apiUrl;

    @GetMapping("/categories/{sortBy}")
    public String index(Model model, @RequestParam int categoryId,
                        @RequestParam(defaultValue = "1") int pageNo,
                        @PathVariable("sortBy") String sortBy) {
        String url = apiUrl + "/categories/" + categoryId + "?sort=" + sortBy + "&pageNo=" + (pageNo-1);
        List<Course> courses;
        try{
            courses = List.of(restTemplate.getForObject(url, Course[].class));
        }catch (Exception ex){
            courses = new ArrayList<>();
        }
        model.addAttribute("courses", courses);
        model.addAttribute("pageNo", pageNo);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("paginationBy", "category");
        url = apiUrl + "/categories/total-course/" + categoryId;
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

    @GetMapping("")
    public String allCategory(Model model) {
        String url = apiUrl + "/categories/";
        List<Category> categories = List.of(restTemplate.getForObject(url, Category[].class));
        model.addAttribute("categories", categories);
        return "header";
    }
}
