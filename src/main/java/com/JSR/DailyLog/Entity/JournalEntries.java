package com.JSR.DailyLog.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "JournalEntries")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class JournalEntries {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Title should be 5-100 characters, no special characters like @,#,$,%,^,&,*
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 5, max = 100, message = "Title must be between 5 and 100 characters")
    @Pattern(regexp = "^[A-Za-z0-9\\s.,'\"!?()-]+$", message = "Title contains invalid characters")
    private String title;

    // Content should be 20-5000 characters
    @Lob
    @NotBlank(message = "Content cannot be blank")
    @Size(min = 20, max = 5000, message = "Content must be between 20 and 5000 characters")
    private String content;

    // Optional category, but if present must be alphanumeric and 3-30 characters
    @Size(max = 30, message = "Category can't exceed 30 characters")
    @Pattern(regexp = "^[A-Za-z0-9\\s]*$", message = "Category must be alphanumeric")
    private String category;

    @CreationTimestamp
    private LocalDateTime createdAt;



    @UpdateTimestamp
    private LocalDateTime updatedAt;


    @ManyToOne(
            fetch = FetchType.LAZY

    )
    @JoinColumn(
        name = "userId",
            referencedColumnName = "userId",
            nullable = false

    )
    @JsonBackReference
    private Users users; // todo -->  A reference to the User who owns this journal entry

    @Override
    public String toString () {
        return "JournalEntries{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", category='" + category + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    // todo-->  this is child table
}
