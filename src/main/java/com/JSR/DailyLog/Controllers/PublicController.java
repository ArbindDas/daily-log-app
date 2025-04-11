package com.JSR.DailyLog.Controllers;

import com.JSR.DailyLog.Entity.Users;
import com.JSR.DailyLog.Services.CustomUsersDetailsService;
import com.JSR.DailyLog.Services.UsersService;
import com.JSR.DailyLog.utills.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private static final Logger logger = LoggerFactory.getLogger(PublicController.class); // Initialize logger


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUsersDetailsService customUsersDetailsService;


    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsersService userService;

    public record printHello(String message) {}

    /**
     * Endpoint to print a hello message.
     * TODO: Consider adding localization (i.e., supporting multiple languages) for messages.
     * TODO: Extend this method for additional greeting types or dynamic messages.
     */
    @GetMapping("/print")
    public printHello print() {
        // TODO: Make the greeting message configurable or dynamic if needed in future requirements.
        return new printHello("jai shree ram prabhu ......");
    }

    /**
     * Endpoint to create a new user.
     * TODO: Add validation checks for the user's data (e.g., email format, password strength).
     * TODO: Enhance the error handling for specific cases (e.g., if the email is already in use).
     * TODO: Consider adding a response body with the created user's ID or other details.
     */
    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody @Valid Users users) {
        // Log instance for creating user
        logger.info("Creating new user: {}", users);

        try {
            // TODO: Consider adding custom validation or business logic before creating the user.

            boolean isUserCreated = userService.saveNewUser(users);

            if (isUserCreated) {
                // TODO: Return more meaningful response, such as the created user's ID or username.
                logger.info("User created successfully: {}", users);
                return new ResponseEntity<>(isUserCreated, HttpStatus.CREATED);
            } else {
                // Log failure message if user creation is not successful
                logger.warn("User creation failed for: {}", users);
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            // Log the exception details
            logger.error("Error occurred while creating user: {}", e.getMessage(), e);
            return new ResponseEntity<>("An error occurred while creating the user.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?>signup(@RequestBody @Valid Users users){
        boolean savedUser = userService.saveNewUser ( users );
        if ( savedUser ){
            return new ResponseEntity<> ( savedUser , HttpStatus.CREATED );
        }else {
            return new ResponseEntity<> ( HttpStatus.BAD_REQUEST );
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?>login(@RequestBody @Valid Users users){
        try {
            authenticationManager.authenticate ( new UsernamePasswordAuthenticationToken ( users.getUsername (), users.getPassword () ) );
            UserDetails userDetails =    customUsersDetailsService.loadUserByUsername ( users.getUsername () );
            String jwt = jwtUtil.generateToken ( userDetails.getUsername () );
            return new ResponseEntity<> ( jwt , HttpStatus.OK );
        } catch ( Exception e ) {
            logger.error ( "Exception occurred while creatingAuthenticationToken ", e );
            return new ResponseEntity<> ( "Incorrect username or password  ", HttpStatus.BAD_REQUEST );
        }
    }
}
