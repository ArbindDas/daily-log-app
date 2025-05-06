package com.JSR.DailyLog.Repository;

import com.JSR.DailyLog.Entity.Users;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserRepositoryImplTest {

    @Autowired
    private UserRepositoryImpl userRepository;




    @Test
    void testFindUserByEmailAndSentimentAnalysis() {
        // Provide the email and sentiment analysis condition you want to check
        String email = "dasarbind269@gmail.com";
        String sentimentAnalysis = "neutral";  // Change this as needed

        Users foundUser = userRepository.getUserByEmailAndSentimentAnalysis(email, sentimentAnalysis);

        Assertions.assertNotNull(foundUser);  // Check if a user is found
        Assertions.assertEquals(email, foundUser.getEmail());  // Check if the correct email matches
    }

}