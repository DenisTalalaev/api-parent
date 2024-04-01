package by.salary.servicetest.controller;

import by.salary.servicetest.service.TestService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@RequestMapping("/test")
@AllArgsConstructor
@Slf4j
public class ServiceTestController {


    TestService testService;
    @GetMapping
    public String index(HttpServletRequest request) {
        log.info(request.getAttributeNames().toString());

        return testService.test();
    }

    @GetMapping("/{id}")
    public Integer index(@PathVariable String id) {
        return Integer.parseInt(id);
    }
}
