package com.JSR.DailyLog.Repository;

import com.JSR.DailyLog.Entity.JournalEntries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface JournalEntriesRepository extends JpaRepository< JournalEntries , Long > {

//    Optional< JournalEntries> findById ( Long journalId );
}
