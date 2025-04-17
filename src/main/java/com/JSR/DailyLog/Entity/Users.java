package com.JSR.DailyLog.Entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "Users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    // User's name (required, length 3-100)
    @NotBlank (message = "Username cannot be blank")
    @Size (min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    private String username;

    // User's email (required, must be a valid email)
    @NotBlank(message = "Email cannot be blank")
    @Email (message = "Email should be valid")
    private String email;


    @Column(name = "sentiment_analysis")
    private boolean sentimentAnalysis;

    // User's password (required, length 8-100)
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;





    @Column(name = "role", nullable = false, length = 255)
    @Convert(converter = RoleListConverter.class)
    private List<String>roles;

    // User can have many journal entries
    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL ,   fetch = FetchType.EAGER  , orphanRemoval = true )
    @JsonManagedReference
    private List<JournalEntries> journalEntries;

    // -->  this is parent table

    @Override
    public String toString() {
        return "Users{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", role='" + roles + '\'' +
                ", password='" + password + '\'' +
                '}';
    }


}

