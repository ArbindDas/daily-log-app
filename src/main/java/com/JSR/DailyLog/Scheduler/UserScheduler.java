package com.JSR.DailyLog.Scheduler;

import com.JSR.DailyLog.Cache.AppCache;
import com.JSR.DailyLog.Entity.JournalEntries;
import com.JSR.DailyLog.Entity.Users;
import com.JSR.DailyLog.Repository.UserRepositoryImpl;
import com.JSR.DailyLog.Services.EmailService;
import com.JSR.DailyLog.Services.SentimentAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserScheduler {



    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;


    @Autowired
    private EmailService emailService;


    @Autowired
    private UserRepositoryImpl userRepository;


    @Autowired
    private AppCache appCache;



    @Scheduled(cron = "0 9 * * SUN")
    public  void  fetchUsersAndSendMail(){

        List< Users > usersList = userRepository.getUserForSA ();

        for (Users users : usersList){
            List< JournalEntries > journalEntries  = users.getJournalEntries ();
            List< String > filterEntries = journalEntries.stream ( ).filter (
                    x -> x.getCreatedAt ( ).isAfter ( LocalDateTime.now ( ).minus ( 7 , ChronoUnit.DAYS ) ) )
                    .map ( x -> x.getContent () ).collect ( Collectors.toList ( ) );

            String entry = String.join ( " "  , filterEntries);
            String sentiment = sentimentAnalysisService.getSentiment ( entry );
            emailService.sendEmail ( users.getEmail () , "senitment for last 7 days " , sentiment );

        }
    }


    @Scheduled(cron = "0 0 /10 * ? * *")
    public void clearAppCache(){
        appCache.init ();
    }
}
