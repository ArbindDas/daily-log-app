package com.JSR.DailyLog.Repository;

import com.JSR.DailyLog.Entity.ConfigJournalAppEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigJournalAppEntityRepository extends JpaRepository< ConfigJournalAppEntity, Long > {


}
