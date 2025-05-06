package com.JSR.DailyLog.Controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping ("/")
    public String home() {
        return "your app successfully running ....";
    }

}
