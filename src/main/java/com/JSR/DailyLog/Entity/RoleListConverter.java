package com.JSR.DailyLog.Entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class RoleListConverter implements AttributeConverter<List<String>, String> {

    // private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return null;
        }
        // Store the roles as comma-separated values (CSV) in the database
        return String.join(",", roles);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return List.of();
        }
        // Split the CSV data into a List and ensure each role has the 'ROLE_' prefix
        return List.of(dbData.split(",")).stream()
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .collect(Collectors.toList());
    }

}
