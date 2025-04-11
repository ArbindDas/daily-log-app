package com.JSR.DailyLog.Controllers;

import com.JSR.DailyLog.Entity.Users;
import com.JSR.DailyLog.Exception.UserNotFoundException;
import com.JSR.DailyLog.Services.UsersService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    // This method fetches all the users from the database and returns them in the response.
    @GetMapping("/get-allUsers")
    public ResponseEntity<?> getAllUsers() {
        try {
            // Get the currently authenticated user's name for logging purposes.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUser = authentication.getName();

            // Log the attempt to fetch all users.
            logger.info("Authenticated user {} is attempting to fetch all users", authenticatedUser);

            // Retrieve the list of all users from the service layer.
            List<Users> users = usersService.usersList();

            // Log the success message after retrieving the users list.
            logger.info("Successfully fetched {} users.", users.size());

            // Return the list of users in the response body with HTTP status OK.
            return new ResponseEntity<>(users, HttpStatus.OK);

        } catch (Exception e) {
            // Log the error if an exception occurs during the fetching process.
            logger.error("Error fetching users: {}", e.getMessage(), e);

            // Return a generic error response indicating failure to fetch users.
            return new ResponseEntity<>("Failed to fetch users. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // This method deletes the user based on the authenticated user's username.
    @DeleteMapping()
    public ResponseEntity<?> deleteUserByUserName() {
        try {
            // Get the currently authenticated user's username for the deletion request.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Log the attempt to delete the user by their username.
            logger.info("Attempting to delete user with username: {}", username);

            // Call the service method to delete the user by their username.
            usersService.deleteByUserName(username);

            // Log the success message after user deletion.
            logger.info("Successfully deleted user with username: {}", username);

            // Return the response with HTTP status NO_CONTENT indicating successful deletion.
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            // Log the error if an exception occurs during the deletion process.
            logger.error("Error deleting user with username: {}", e.getMessage(), e);
            // Return a generic error response indicating failure to delete user.
            return new ResponseEntity<>("Failed to delete user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // This method fetches a user by their ID.
    @GetMapping("/id/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable Long userId) {
        try {
            // Get the currently authenticated user's username for logging purposes.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUsername = authentication.getName();

            // Log the attempt to retrieve the user by their ID.
            logger.info("Authenticated user: {} is attempting to fetch user with ID: {}", authenticatedUsername, userId);

            // Call the service to get the user by ID.
            Optional<Users> user = usersService.getUsersById(userId);

            // Check if the user is found and return appropriate response.
            if (user.isPresent()) {
                Users users = user.get();
                logger.info("Successfully retrieved user with ID: {}", userId);
                logger.info("user get : {}", users);
                return new ResponseEntity<>(users, HttpStatus.OK);
            } else {
                // Log the warning if the user is not found.
                logger.warn("User with ID: {} not found", userId);
                // Return 404 Not Found if the user is not found.
                return new ResponseEntity<>("User with ID " + userId + " not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Log the error if an exception occurs while fetching the user by ID.
            logger.error("Error fetching user with ID: {}", userId, e);

            // Throw custom exception if user is not found.
            throw new UserNotFoundException("User not found for " + userId);
        }
    }

    // This method fetches a user by their username.
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUserName(@PathVariable String username) {
        try {
            // Get the currently authenticated user's username for logging purposes.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUsername = authentication.getName();

            // Log the attempt to retrieve the user by their username.
            logger.info("Authenticated user: {} is attempting to fetch user with username: {}", authenticatedUsername, username);

            // Call the service to find the user by their username.
            Optional<Users> optionalUsers = usersService.findUserByUserName(username);
            if (optionalUsers.isPresent()) {
                // Log the success message after retrieving the user by username.
                logger.info("Successfully retrieved user with username: {}", username);
                return new ResponseEntity<>(optionalUsers.get(), HttpStatus.OK);
            } else {
                // Log the warning if the user is not found by username.
                logger.warn("User with username: {} not found", username);
                // Return 404 Not Found if the user is not found.
                return new ResponseEntity<>("User with username " + username + " not found", HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            // Log the error if an exception occurs during the fetching process.
            logger.error("Error fetching user with username: {}", username, e);

            // Throw custom exception if user is not found.
            throw new UserNotFoundException("User not found for " + username);
        }
    }

    // This method updates the user information based on the authenticated user's data.
    @PutMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestBody @Valid Users updatedUser) {
        try {
            // Get the currently authenticated user's username.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Call the service to find the existing user by their username.
            Optional<Users> optionalUsers = usersService.findUserByUserName(username);
            if (optionalUsers.isPresent()) {
                // Retrieve the existing user and update their information.
                Users existingUser = getUsers(updatedUser, optionalUsers);

                // Save the updated user and return response with status CREATED.
                usersService.saveNewUser(existingUser);
                return new ResponseEntity<>(existingUser, HttpStatus.CREATED);
            } else {
                // Return 404 Not Found if the user to update is not found.
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            // Log the error if an exception occurs during the update process.
            logger.error("Error updating user", e);
            // Return a generic error response.
            return new ResponseEntity<>("Failed to update user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // This helper method updates fields of an existing user with the provided updated data.
    private static Users getUsers(Users updatedUser, Optional<Users> optionalUsers) {
        Users existingUser = optionalUsers.get();

        // Update email if it is not null or blank.
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty() && !updatedUser.getEmail().isBlank()) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        // Update username if it is not null or blank.
        if (updatedUser.getUsername() != null && !updatedUser.getUsername().isEmpty() && !updatedUser.getUsername().isBlank()) {
            existingUser.setUsername(updatedUser.getUsername());
        }
        // Update password if it is not null or blank.
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existingUser.setPassword(updatedUser.getPassword());
        }

        // Return the updated user.
        return existingUser;
    }
}
