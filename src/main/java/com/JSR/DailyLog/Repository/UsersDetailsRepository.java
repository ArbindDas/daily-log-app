package com.JSR.DailyLog.Repository;

import com.JSR.DailyLog.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersDetailsRepository extends JpaRepository< Users , Long > {
        Users findByUsername( String username);


       void deleteByUsername(String username);

    // Get users where sentimentAnalysis is true and email is valid
    @Query ("SELECT u FROM Users u WHERE u.sentimentAnalysis = true AND u.email LIKE '%@%.%'")
    List< Users > getUsersForSentimentAnalysis();

    // New query to check for a specific email and sentiment analysis value
    @Query("SELECT u FROM Users u WHERE u.email = :email AND u.sentimentAnalysis = :sentimentAnalysis")
    Optional<Users> findUserByEmailAndSentimentAnalysis( @Param("email") String email, @Param ("sentimentAnalysis") boolean sentimentAnalysis);
}
