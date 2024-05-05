package net.wuxianjie.limitsserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class LimitsController {

    @GetMapping("/limits")
    public Limits getLimits() {
        return new Limits(1, 1000);
    }

}