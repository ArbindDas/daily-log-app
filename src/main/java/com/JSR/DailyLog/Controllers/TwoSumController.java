package com.JSR.DailyLog.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sum")
public class TwoSumController {


//    public TwoSumController ( int ans ) {
//    }

    public record  sumofTwoNumber(int ans){}

    @GetMapping("/add")
    public sumofTwoNumber sumofTwoNumber( @RequestParam int num1 , @RequestParam int num2){
        int ans = num1 + num2;
        return new sumofTwoNumber (ans);
    }


}
