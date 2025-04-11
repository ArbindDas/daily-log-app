package com.JSR.DailyLog.Controllers;

import com.JSR.DailyLog.Entity.Users;
import com.JSR.DailyLog.Services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UsersService usersService;

    /**
     * Endpoint to fetch all users.
     * todo: Add pagination and sorting to improve performance when fetching large user lists.
     * // TODO: Handle null user condition.
     */
    @GetMapping("/all-users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<Users> allUsers = usersService.usersList();

            // todo: Add proper logging here for successful response, maybe include the number of users returned
            if (allUsers != null && !allUsers.isEmpty()) {
                return new ResponseEntity<>(allUsers, HttpStatus.OK);
            }

            // todo: Consider returning a custom message body with the BAD_REQUEST status for better clarity
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        } catch (RuntimeException e) {
            System.out.println(e);  // todo: Replace with a proper logging framework (e.g., SLF4J, Logback)
            // todo: Return more meaningful error details for debugging and troubleshooting
            throw new RuntimeException("An error occurred while fetching all users: " + e.getMessage());
        }
    }

    /**
     * Endpoint to create a new admin user.
     * todo: Validate admin user data before saving to the database (e.g., check if email is already taken).
     * todo: Add authorization checks to ensure only authorized users can create new admins.
     */
    @PostMapping("/create-new-admin")
    public ResponseEntity<?> createAdmin(Users adminUser) {
        try {
            // todo--> Validate user data (e.g., password strength, email format, etc.) before creating an admin
              
            usersService.saveAdmin(adminUser);

            // todo: Consider returning a response body with details of the created admin (e.g., username or ID)
            return new ResponseEntity<>(HttpStatus.CREATED);

        } catch (RuntimeException e) {
            System.out.println(e);  // todo: Replace with a proper logging framework (e.g., SLF4J, Logback)
            // todo: Return more meaningful error details for debugging and troubleshooting
            throw new RuntimeException("An error occurred while creating admin: " + e.getMessage());
        }
    }
}
