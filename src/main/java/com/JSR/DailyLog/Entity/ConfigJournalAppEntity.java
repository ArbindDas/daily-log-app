package com.JSR.DailyLog.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(
        name = "ConfigJournalAppEntity_tbl"
)
@NoArgsConstructor
public class ConfigJournalAppEntity {


    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    @NotBlank
    @Column(name = "`key`", nullable = false)
    private String  key;

    @NotBlank
    @Column(name = "value", nullable = false)
    private String value;

}
