package com.JSR.DailyLog;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.dialect.MySQLDialect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class DailyLogApplication {

	public static void main(String[] args) {
		SpringApplication.run(DailyLogApplication.class, args);
	}



	@Bean(name = "transactionManager")
	public PlatformTransactionManager platformTransactionManager( EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager (entityManagerFactory);
	}


	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}


}
