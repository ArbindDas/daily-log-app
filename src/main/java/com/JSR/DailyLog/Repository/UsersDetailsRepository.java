package com.JSR.DailyLog.Repository;

import com.JSR.DailyLog.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersDetailsRepository extends JpaRepository< Users , Long > {
        Users findByUsername( String username);


       void deleteByUsername(String username);

}
