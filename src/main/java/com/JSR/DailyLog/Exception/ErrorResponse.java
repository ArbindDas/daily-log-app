package com.JSR.DailyLog.Exception;


import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ErrorResponse {
    private LocalDateTime localDateTime;
    private int status;
    private String error;
    private String message;
    private String path;
}
