package com.JSR.DailyLog.Controllers;


import com.JSR.DailyLog.Entity.JournalEntries;
import com.JSR.DailyLog.Entity.Users;
import com.JSR.DailyLog.Exception.JournalEntriesNotFoundException;
import com.JSR.DailyLog.Services.JournalEntriesService;
import com.JSR.DailyLog.Services.UsersService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping ("/api/journal")

public class JournalEntriesController {
    private static final Logger logger = LoggerFactory.getLogger ( JournalEntriesController.class );
    @Autowired
    private JournalEntriesService journalEntriesService;

    @Autowired
    private UsersService usersService;


    @GetMapping ()
    @Transactional (propagation = Propagation.REQUIRES_NEW, isolation = Isolation.SERIALIZABLE)
    public ResponseEntity < ? > getAllJournalEntriesByUsername () {

        try {

            Authentication authentication = SecurityContextHolder.getContext ( ).getAuthentication ( );

            String username = authentication.getName ( );

            Optional < Users > usersOptional = usersService.findUserByUserName ( username );

            if ( usersOptional.isPresent ( ) ) {
                List < JournalEntries > journalEntriesList = usersOptional.get ( ).getJournalEntries ( );

                if ( journalEntriesList != null && ! journalEntriesList.isEmpty ( ) ) {
                    return new ResponseEntity <> ( journalEntriesList , HttpStatus.OK );
                } else {
                    return new ResponseEntity <> ( "No journal entries found" , HttpStatus.NOT_FOUND );
                }
            } else {
                return new ResponseEntity <> ( "User not found" , HttpStatus.NOT_FOUND );
            }

        } catch ( RuntimeException e ) {
            e.printStackTrace ( );
            return new ResponseEntity <> ( "An error occurred" , HttpStatus.INTERNAL_SERVER_ERROR );
        }
    }


    @PostMapping ()
    @Transactional (propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE)
    public ResponseEntity < ? > createEntry ( @RequestBody @Valid JournalEntries journalEntries ) {
        try {

            Authentication authentication = SecurityContextHolder.getContext ( ).getAuthentication ( );
            String username = authentication.getName ( );

            journalEntriesService.saveJournalEntriesWithUsers ( journalEntries , username );
            logger.info ( "Succesfully journal added for the user : {} and the journal is {}", username  , journalEntries);
            return new ResponseEntity <> ( journalEntries , HttpStatus.CREATED );
        } catch ( IllegalArgumentException e ) {
            return new ResponseEntity <> ( "Error: " + e.getMessage ( ) , HttpStatus.NOT_FOUND );
        } catch ( Exception e ) {
            e.printStackTrace ( );
            return new ResponseEntity <> ( "An unexpected error occurred: " + e.getMessage ( ) , HttpStatus.INTERNAL_SERVER_ERROR );
        }

    }


    @GetMapping ("/id/{myid}")
    public ResponseEntity < ? > getJournalById ( @PathVariable Long myid ) {
        try {
            Authentication authentication = SecurityContextHolder.getContext ( ).getAuthentication ( );
            String username = authentication.getName ( );

            Optional < Users > usersOptional = usersService.findUserByUserName ( username );
            if ( usersOptional.isPresent ( ) ) {
                List < JournalEntries > collect = usersOptional.get ( ).getJournalEntries ( ).stream ( )
                        .filter ( x -> x.getId ( ).equals ( myid ) )
                        .collect ( Collectors.toList ( ) );

                if ( ! collect.isEmpty ( ) ) {
                    Optional < JournalEntries > journalEntries = journalEntriesService.findJournalById ( myid );
                    if ( journalEntries.isPresent ( ) ) {
                        return new ResponseEntity <> ( journalEntries.get ( ) , HttpStatus.OK );  // Unwrap Optional
                    } else {
                        return new ResponseEntity <> ( "JournalEntry not found" , HttpStatus.NOT_FOUND );
                    }
                }
            }
        } catch ( RuntimeException e ) {
            throw new JournalEntriesNotFoundException ( e.getMessage () );
        }
        return new ResponseEntity <> ( "An error occurred" , HttpStatus.INTERNAL_SERVER_ERROR );
    }


    @DeleteMapping("/id/{myid}")
    public ResponseEntity<?> deleteJouralbyId(@PathVariable Long myid) {
        try {
          
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            logger.info("Attempting to delete JournalEntry with ID: {} for user: {}", myid, username);

            boolean removed = journalEntriesService.deleteById(myid, username);

            if (removed) {
                logger.info("JournalEntry with ID: {} deleted successfully", myid);
                return new ResponseEntity<>(true, HttpStatus.NO_CONTENT);
            } else {
                logger.warn("JournalEntry with ID: {} not found", myid);
                return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            logger.error("Error deleting JournalEntry with ID: {}", myid, e);
            throw new RuntimeException("An error occurred while deleting journal entry", e);
        }
    }



    @PutMapping ("/id{myid}")
    public ResponseEntity < ? > updateJournalById ( @PathVariable Long myid , @Valid @RequestBody JournalEntries updatedJournal ) {

        try {

            Authentication authentication = SecurityContextHolder.getContext ( ).getAuthentication ( );
            String username = authentication.getName ( );

            Optional < Users > usersOptional = usersService.getUsersById ( myid );

            List < JournalEntries > collect = usersOptional.get ( ).getJournalEntries ( ).stream ( )
                    .filter ( x -> x.getId ( ).equals ( myid ) ).collect ( Collectors.toList ( ) );

            if ( ! collect.isEmpty ( ) ) {

                Optional < JournalEntries > journalEntriesOptional = journalEntriesService.findJournalById ( myid );
                if ( journalEntriesOptional.isPresent ( ) ) {
                    JournalEntries existingJournal = journalEntriesOptional.get ( );

                    if ( existingJournal.getTitle ( ) != null && ! existingJournal.getTitle ( ).isEmpty ( ) ) {
                        existingJournal.setTitle ( updatedJournal.getTitle ( ) );
                    }

                    if ( existingJournal.getContent ( ) != null && ! existingJournal.getContent ( ).isEmpty ( ) ) {
                        existingJournal.setContent ( updatedJournal.getContent ( ) );
                    }

                    if ( existingJournal.getCategory ( ) != null && ! existingJournal.getCategory ( ).isEmpty ( ) ) {
                        existingJournal.setCategory ( updatedJournal.getCategory ( ) );
                    }

                    return new ResponseEntity <> ( existingJournal , HttpStatus.CREATED );
                }
            }

        } catch ( RuntimeException e ) {
            throw new RuntimeException ( e );
        }
        return new ResponseEntity <> ( HttpStatus.NOT_FOUND );
    }


}
