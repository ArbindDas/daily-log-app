package com.JSR.DailyLog.Repository;




import com.JSR.DailyLog.Entity.Users;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Optional;

@Component
public class UserRepositoryImpl {

    @Autowired
    private UsersDetailsRepository userRepository;


    public List< Users > getUserForSA() {
        return userRepository.getUsersForSentimentAnalysis();
    }


    public Users getUserByEmailAndSentimentAnalysis(String email, boolean sentimentAnalysis) {
        Optional<Users> user = userRepository.findUserByEmailAndSentimentAnalysis(email, sentimentAnalysis);

        if (user.isPresent()) {
            return user.get();  // Return the found user
        } else {
            return null;  // User not found
        }
    }
}
