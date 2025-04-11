package com.JSR.DailyLog.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping ("/health-check")
    public String healthcheck(){
        return "check up succesfully Done  : ";
    }
}
