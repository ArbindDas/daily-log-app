package com.JSR.DailyLog.Cache;

import com.JSR.DailyLog.Entity.ConfigJournalAppEntity;
import com.JSR.DailyLog.Repository.ConfigJournalAppEntityRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AppCache {



    public enum  keys{
        weather_api;
    }

    @Autowired
    private ConfigJournalAppEntityRepository configJournalAppEntityRepository;


//    public Map<String , String> APP_CACHE = new HashMap<> (  );
    public Map<String , String> APP_CACHE;


//    🔥 Why is this caching useful?
//    Now whenever your application needs the weather_api URL,
//
//    It can quickly get it from APP_CACHE (no need to go to the database every time).
//
//    Much faster and efficient for frequent reads.



    @PostConstruct()
    public void init(){
        APP_CACHE = new HashMap<> (  );
        List< ConfigJournalAppEntity > appEntityRepositoryAll = configJournalAppEntityRepository.findAll ( );
        for (ConfigJournalAppEntity configJournalAppEntity : appEntityRepositoryAll){
            APP_CACHE.put ( configJournalAppEntity.getKey ( ) , configJournalAppEntity.getValue ( ) );
        }
    }


}
