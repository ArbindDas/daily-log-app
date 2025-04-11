package com.JSR.DailyLog.Services;

import com.JSR.DailyLog.Entity.JournalEntries;
import com.JSR.DailyLog.Entity.Users;
import com.JSR.DailyLog.Repository.JournalEntriesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class JournalEntriesService {


    private static final Logger logger = LoggerFactory.getLogger ( JournalEntriesService.class );
    @Autowired
    private JournalEntriesRepository journalEntriesRepository;

    @Autowired
    private UsersService usersService;

    //--> Method to save a journal entry associated with a specific user identified by username
    @Transactional(propagation = Propagation.REQUIRED)
    @CachePut(value = "users", key = "#username")  // Evict cached user data by username
    public void saveJournalEntriesWithUsers( JournalEntries journalEntries, String username) {
        try {
            // Retrieve the user by username
            Optional < Users > optionalUsers = usersService.findUserByUserName ( username );

            // Check if the user is found
            if ( optionalUsers.isPresent ( ) ) {
                Users users = optionalUsers.get ( );

                // Set timestamps for the journal entry
                journalEntries.setCreatedAt ( LocalDateTime.now ( ) );
                journalEntries.setUpdatedAt ( LocalDateTime.now ( ) );

                // Associate the user with the journal entry before saving
                journalEntries.setUsers ( users );

                // Save the journal entry and link it to the user
                JournalEntries savedJournalEntries = journalEntriesRepository.save ( journalEntries );

                // Add the saved journal entry to the user's list (if it's a bidirectional relationship)
                users.getJournalEntries ( ).add ( savedJournalEntries );

                // Save the updated user with the new journal entry
                usersService.saveUser ( users );

            } else {
                throw new RuntimeException ( "User not found" ); // User not found, throw exception
            }
        }catch (DataAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error occurred: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error occurred: " + e.getMessage());
        }


    }


    //--> Method to save a journal entry without associating it with a user
    //--> Method to save a journal entry without associating it with a user
    @Transactional(propagation =  Propagation.REQUIRES_NEW ,isolation = Isolation.SERIALIZABLE)
    @CachePut (value = "JournalEntries", key = "#journalEntries.id") // Cache the saved journal entry
    public JournalEntries saveEntry(JournalEntries journalEntries) {
        try {
            logger.info("Attempting to save journal entry without user association...");
            JournalEntries savedEntry = journalEntriesRepository.save(journalEntries);
            logger.info("Journal entry saved successfully with ID: {}", savedEntry.getId());
            return savedEntry;
        } catch (Exception e) {
            logger.error("Error occurred while saving journal entry: ", e); // Log error if exception occurs
            throw new RuntimeException("Failed to save journal entry", e);
        }
    }


    //--> Method to retrieve a list of all journal entries
    @Transactional(propagation = Propagation.REQUIRES_NEW  , isolation = Isolation.SERIALIZABLE)
    @Cacheable (value = "JournalEntries", key = "'allJournalEntries'", unless = "#result.isEmpty()")
    public List<JournalEntries> journalEntriesList() {
        try {
            logger.info("Fetching all journal entries...");
            List<JournalEntries> entries = journalEntriesRepository.findAll();
            logger.info("Successfully retrieved {} journal entries", entries.size());
            return entries;
        } catch (Exception e) {
            logger.error("Error occurred while fetching journal entries: ", e); // Log error if exception occurs
            throw new RuntimeException("Failed to fetch journal entries", e);
        }
    }

    //--> Method to retrieve a specific journal entry by its ID
    @Transactional(propagation = Propagation.REQUIRED )
    @Cacheable(value = "JournalEntries", key = "#journalId", unless = "#result.isEmpty()")
    public Optional<JournalEntries> findJournalById(Long journalId) {
        try {
            logger.info("Attempting to find journal entry with ID: {}", journalId);
            Optional<JournalEntries> journalEntry = journalEntriesRepository.findById(journalId);
            if (journalEntry.isPresent()) {
                // JournalEntries entries = journalEntry.get ();
                logger.info("Journal entry found with ID: {}", journalId);
            } else {
                logger.warn("No journal entry found with ID: {}", journalId);
            }
            return journalEntry;
        } catch (Exception e) {
            logger.error("Error occurred while finding journal entry with ID: " + journalId, e); // Log error if exception occurs
            throw new RuntimeException("Failed to find journal entry", e);
        }
    }

    //--> Method to delete a journal entry by its ID for a specific user
    @Transactional(propagation = Propagation.REQUIRED)
    @CacheEvict(value = "JournalEntries", key = "#journalId")
    public boolean deleteById(Long journalId, String userName) {
        boolean removed = false;
        try {
            // Retrieve the user by username
            Optional<Users> userOptional = usersService.findUserByUserName(userName);

            // Check if the user is found
            if (userOptional.isPresent()) {
                Users user = userOptional.get(); // Unwrap the userOptional

                // Remove the journal entry from the user's list of journal entries
                removed = user.getJournalEntries().removeIf(x -> x.getId ().equals(journalId)); // Modify the journal entries

                // If entry is removed, delete it from the repository
                if (removed) {
                    // Only save the user (not the whole entity)
                    usersService.saveNewUser (user); // Save the updated user without deleting the user entity
                    journalEntriesRepository.deleteById(journalId); // Delete the journal entry by ID
                }
            } else {
                throw new RuntimeException("User not found with username: " + userName); // User not found, throw exception
            }
        } catch (RuntimeException e) {
            System.out.println(e);
            throw new RuntimeException("An error occurred while deleting this id", e); // Error occurred during deletion
        }
        return removed; // Return true if the entry was removed, otherwise false
    }


}
