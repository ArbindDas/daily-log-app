
package com.JSR.DailyLog.Config;

import com.JSR.DailyLog.Filter.JwtFilter;
import com.JSR.DailyLog.Services.CustomUsersDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {


    private  final CustomUsersDetailsService usersDetailsService;
    private final JwtFilter jwtFilter;
    @Autowired
    public SpringSecurityConfig ( CustomUsersDetailsService usersDetailsService , JwtFilter jwtFilter ) {
        this.usersDetailsService = usersDetailsService;
        this.jwtFilter = jwtFilter;
    }



        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/" , "/me", "/api/health/**" ,"/api/public/**").permitAll()
                            .requestMatchers("/api/journal/**", "/api/user/**").authenticated()
                            .requestMatchers("/api/admin/**").hasRole("ADMIN")
                            .anyRequest().permitAll()
                    )
//                    .oauth2Login ( Customizer.withDefaults () );
                    .oauth2Login(oauth2 -> oauth2
                            .defaultSuccessUrl("/me", true)
                    );

            http.addFilterBefore ( jwtFilter , UsernamePasswordAuthenticationFilter.class );

            return http.build();
        }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(usersDetailsService).passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }


}
