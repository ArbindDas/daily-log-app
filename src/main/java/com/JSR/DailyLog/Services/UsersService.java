package com.JSR.DailyLog.Services;

import com.JSR.DailyLog.Entity.Users;
import com.JSR.DailyLog.Repository.UsersDetailsRepository;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    private static final Logger logger = LoggerFactory.getLogger ( UsersService.class );

    @Autowired
    private UsersDetailsRepository usersDetailsRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder ( );

    // todo --> Method to save an admin user, encrypts the password, assigns roles, and saves the user to the database.
    @Transactional (propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    @CachePut(value = "Admin" , key = "'allAdmins'")
    // todo--> If you're caching all admin users (i.e., a list of admins), the @CachePut with key 'allAdmins' is fine.
    //todo-->   If you're caching a single admin user, change the cache key to something unique for the admin
    // (e.g., #adminUser.getUsername() or #adminUser.getId()).
    public void saveAdmin ( Users adminUser ) {
        try {
            logger.info ( "Attempting to save admin user with username: {}" , adminUser.getUsername ( ) );

            // Encrypt the password before saving
            adminUser.setPassword ( passwordEncoder.encode ( adminUser.getPassword ( ) ) );
            // Assign roles for the admin
            adminUser.setRoles ( Arrays.asList ( "USER" , "ADMIN" ) );

            // Save the user to the repository
            usersDetailsRepository.save ( adminUser );

            // Log success
            logger.info ( "Successfully saved admin user with username: {}" , adminUser.getUsername ( ) );

        } catch ( Exception e ) {
            // Log the error if an exception occurs
            logger.error ( "Error saving admin user with username: {}" , adminUser.getUsername ( ) , e );
            throw new RuntimeException ( "Failed to save admin user" , e );
        }
    }

    // todo --> Method to save a normal user, logs details before saving, and catches errors during saving.
    @Transactional (propagation = Propagation.REQUIRED)
    public Users saveUser ( Users newUser ) {
        try {
            // Log user details before saving
            logger.info ( "Saving user: {}" , newUser );

            return usersDetailsRepository.save ( newUser );

        } catch ( Exception e ) {
            // Log the error with the logger
            logger.error ( "Error saving user: {}" , e.getMessage ( ) , e );
            throw new RuntimeException ( "Failed to save user" , e );
        }
    }

    // todo --> Method to save or update a user. It encodes the password, assigns a role, and logs success or failure.
    @Transactional (propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    @CachePut (value = "usersList", key = "'allUsers'")
    public boolean saveNewUser ( Users users ) {
        try {
            // Log the attempt to save or update a user
            logger.info ( "Attempting to save or update user with username: {}" , users.getUsername ( ) );

            // Encrypt the password before saving
            users.setPassword ( passwordEncoder.encode ( users.getPassword ( ) ) );
            // Assign default role

            // ✅ Assign default role if not present
            if ( users.getRoles ( ) == null || users.getRoles ( ).isEmpty ( ) ) {
                users.setRoles ( List.of ( "ROLE_USER" ) ); // assign default
            }

            // Save the user to the repository
            usersDetailsRepository.save ( users );

            // Log success
            logger.info ( "Successfully saved or updated user with username: {}" , users.getUsername ( ) );
            return true;
        } catch ( RuntimeException e ) {
            // Log the error if an exception occurs
            logger.error ( "Error saving or updating user with username: {}" , users.getUsername ( ) , e );
            throw new RuntimeException ( "Failed to save or update user" , e );
        }
    }


    // todo --> Method to retrieve a list of all users from the database and log the process.
    @Transactional (propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
//    @Cacheable(value = "usersList", key = "'allUsers'", unless = "#result.isEmpty()")
//    @Cacheable(value = "usersList", key = "#root.methodName")
    @CachePut(value = "usersList", key = "#root.methodName")
    public List< Users > usersList () {
        try {
            // Log the retrieval of users
            logger.info ( "Retrieving the list of all users." );

            // Get the list of users from the repository
            List< Users > users = usersDetailsRepository.findAll ( );

            // Log the size of the retrieved users list
            logger.info ( "Successfully retrieved {} users." , users.size ( ) );

            return users;
        } catch ( Exception e ) {
            // Log the error if an exception occurs
            logger.error ( "Error retrieving users list" , e );
            throw new RuntimeException ( "Failed to retrieve users list" , e );
        }
    }


    // todo --> Method to retrieve a user by their ID. Logs the attempt and the result (success or not found).
    @Transactional (propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    @Cacheable (value = "usersById", key = "#userId", unless = "#result.isEmpty()")
    public Optional< Users > getUsersById ( Long userId ) {
        try {
            // Log the attempt to retrieve the user
            logger.info ( "Attempting to retrieve user with ID: {}" , userId );

            Optional< Users > user = usersDetailsRepository.findById ( userId );

            // Log the result
            if ( user.isPresent ( ) ) {
                logger.info ( "Successfully retrieved user with ID: {}" , userId );
            } else {
                logger.warn ( "User with ID: {} not found" , userId );
            }

            return user;

        } catch ( Exception e ) {
            // Log the error if an exception occurs
            logger.error ( "Error retrieving user with ID: {}" , userId , e );
            throw new RuntimeException ( "Failed to retrieve user" , e );
        }
    }

    // todo --> Method to find a user by their username. Logs the process and outcome (found or not found).
    @Transactional (propagation = Propagation.REQUIRED)
    @Cacheable (value = "users", key = "#username", unless = "#result == null")
    public Optional< Users > findUserByUserName ( String username ) {
        try {
            // Log the attempt to find the user by username
            logger.info ( "Attempting to find user with username: {}" , username );

            // Retrieve the user by username
            Users user = usersDetailsRepository.findByUsername ( username );

            // Log the result
            if ( user != null ) {
                logger.info ( "Successfully found user with username: {}" , username );
                return Optional.of ( user );
            } else {
                logger.warn ( "User with username: {} not found" , username );
                return Optional.empty ( );
            }
        } catch ( Exception e ) {
            // Log the error if an exception occurs
            logger.error ( "Error finding user with username: {}" , username , e );
            throw new RuntimeException ( "Failed to find user" , e );
        }
    }

    // todo --> Method to delete a user by their ID. Logs success or failure (not found or deleted).
    @Transactional (propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    @CacheEvict (value = "usersById", key = "#userId")
    public boolean deleteUserById ( Long userId ) {
        try {
            Optional< Users > optionalUsers = usersDetailsRepository.findById ( userId );
            if ( optionalUsers.isPresent ( ) ) {

                usersDetailsRepository.deleteById ( userId );

                // Log success when user is deleted
                logger.info ( "Successfully deleted user with ID: {}" , userId );
                return true;
            } else {
                // Log if user is not found
                logger.warn ( "User with ID: {} not found, unable to delete" , userId );
                return false;
            }
        } catch ( RuntimeException e ) {
            // Log the error if exception occurs
            logger.error ( "Error deleting user with ID: {}" , userId , e );
            throw new RuntimeException ( "Failed to delete user" , e );
        }
    }

    // todo --> Method to delete a user by their username. Logs the process and outcome (found or not found).
    @Transactional (propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    @CacheEvict (value = "users", key = "#username")
    public void deleteByUserName ( String username ) {
        // Optional: retrieve user before deleting to get userId and evict that cache too
        Users user = usersDetailsRepository.findByUsername ( username );

        if ( user != null ) {

            usersDetailsRepository.deleteByUsername ( username );
            logger.info ( "Deleted user with username: {}" , username );
        } else {
            logger.warn ( "User with username: {} not found, nothing deleted" , username );
        }
    }


}
