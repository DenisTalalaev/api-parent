package by.salary.servicetest.controller;

import by.salary.servicetest.service.TestService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@RequestMapping("/test")
@AllArgsConstructor
public class ServiceTestController {


    TestService testService;
    @GetMapping
    public String index() {
        return testService.test();
    }

    @GetMapping("/{id}")
    public Integer index(@PathVariable String id) {
        return Integer.parseInt(id);
    }
}
