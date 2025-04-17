package com.JSR.DailyLog.Controllers;

import com.JSR.DailyLog.Cache.AppCache;
import com.JSR.DailyLog.Entity.Users;
import com.JSR.DailyLog.Exception.UserNotFoundException;
import com.JSR.DailyLog.Services.UsersService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {


    @Autowired
    private AppCache appCache;

    private final UsersService usersService;

    @Autowired
    public AdminController ( UsersService usersService ) {
        this.usersService = usersService;
    }

    /**
     * Endpoint to fetch all users.
     * : Add pagination and sorting to improve performance when fetching large user lists.
     * : Handle null user condition.
     */
    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<Users> allUsers = usersService.usersList();

            // Add proper logging here for successful response, maybe include the number of users returned
            if (allUsers != null && !allUsers.isEmpty()) {
                return new ResponseEntity<>(allUsers, HttpStatus.OK);
            }

            // Consider returning a custom message body with the BAD_REQUEST status for better clarity
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } catch (RuntimeException e) {
           log.error ( "While getting all users and thier journalEntries are not fetching due to the error {}", e.getMessage () );
            // Return more meaningful error details for debugging and troubleshooting
            throw new UserNotFoundException ("An error occurred while fetching all users: " + e.getMessage());
        }
    }

    /**
     * Endpoint to create a new admin user.
     * : Validate admin user data before saving to the database (e.g., check if email is already taken).
     * : Add authorization checks to ensure only authorized users can create new admins.
     */
    @PostMapping("/create-new-admin")
    public ResponseEntity<?> createAdmin(Users adminUser) {
        try {
            // Validate user data (e.g., password strength, email format, etc.) before creating an admin

            usersService.saveAdmin(adminUser);

            // Consider returning a response body with details of the created admin (e.g., username or ID)
            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (RuntimeException e) {
            log.error ( "while creating an admin we are getting an error {}", e.getMessage () );
            // Return more meaningful error details for debugging and troubleshooting
            throw new RuntimeException("An error occurred while creating admin: " + e.getMessage());
        }
    }


    @GetMapping("/clear-app-cache")
    public void clearAppCache(){
        appCache.init ();
    }

}
