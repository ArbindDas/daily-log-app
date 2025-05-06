package com.JSR.DailyLog.Scheduler;

import com.JSR.DailyLog.Cache.AppCache;
import com.JSR.DailyLog.Entity.JournalEntries;
import com.JSR.DailyLog.Entity.Users;
import com.JSR.DailyLog.Repository.UserRepositoryImpl;
import com.JSR.DailyLog.Services.EmailService;
import com.JSR.DailyLog.Services.SentimentAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserScheduler {

    private static final Logger logger = LoggerFactory.getLogger(UserScheduler.class);


    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private AppCache appCache;

    /**
     * Runs every Sunday at 9:00 AM
     */
//    @Scheduled(cron = "0 0 9 ? * SUN") // Fixed CRON expression
//    @Scheduled(cron = "0 * * * * *")  // Every 1 minute at 0 seconds
    @Scheduled(fixedRate = 60000)
    public void fetchUsersAndSendMail() {
        String sentiment = "positive"; // or compute dynamically if needed

        List<Users> usersList = userRepository.getUserForSA(sentiment);

        for (Users user : usersList) {
            List<JournalEntries> journalEntries = user.getJournalEntries();

            List<String> recentContents = journalEntries.stream()
                    .filter(entry -> entry.getCreatedAt()
                            .isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS)))
                    .map(JournalEntries::getContent)
                    .collect(Collectors.toList());

            if (!recentContents.isEmpty()) {
                String joinedEntry = String.join(" ", recentContents);

                logger.info("Sending sentiment email to {}", user.getEmail());

                emailService.sendEmail(user.getEmail(), "Sentiment for last 7 days", sentiment);
            }
        }
    }


    /**
     * Runs every 10 minutes
     */
    @Scheduled(cron = "0 */10 * * * ?") // Fixed CRON for every 10 minutes
    public void clearAppCache() {
        appCache.init();
    }
}
