package fa.training.frontend.controller;

import fa.training.frontend.model.Order;
import io.jsonwebtoken.Claims;
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
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${courses.api.url}")
    private String apiUrl;

    @GetMapping("history")
    public String index(Model model, @RequestParam(defaultValue = "1") int pageNo) {
        int userId = 3;
        String url = apiUrl + "/order/" + userId;// + "?pageNo=" + (pageNo - 1);
        List<Order> orders;
        try {
            orders = List.of(restTemplate.getForObject(url, Order[].class));
        } catch (Exception ex) {
            orders = new ArrayList<>();
        }
        model.addAttribute("orders", orders);
//        model.addAttribute("pageNo", pageNo);
//        url = apiUrl + "/categories/total-course/" + id;
//        int pageNum = 20;
//        int totalCourse = restTemplate.getForObject(url, Integer.class);
//        int totalPage = totalCourse % pageNum == 0 ? totalCourse / pageNum : totalCourse / pageNum + 1;
//        if (totalPage > 0) {
//            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPage)
//                    .boxed()
//                    .collect(Collectors.toList());
//            model.addAttribute("pageNumbers", pageNumbers);
//            model.addAttribute("totalPage", totalPage);
//        }
        return "history-booking";
    }
}
