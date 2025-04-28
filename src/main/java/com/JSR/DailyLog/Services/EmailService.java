package com.JSR.DailyLog.Services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail ( String to , String subject , String body ) {
        try {
            log.info ( "Sending email to: {}" , to );
            log.info ( "Subject: {}" , subject );
            log.info ( "Body: {}" , body );

            SimpleMailMessage mail = new SimpleMailMessage ( );
            mail.setFrom ( "dasarbind269@gmail.com" );  // Set the sender's email address
            mail.setTo ( to.trim ( ) );
            mail.setSubject ( subject );
            mail.setText ( body );
            javaMailSender.send ( mail );
            log.info ( "Email sent successfully to: " + to );


        } catch ( RuntimeException e ) {
            log.error ( "Exception while sending mail: " , e );
            throw new RuntimeException ( e );
        }
    }


}
