package com.example.enterprise_project_management_platform.dto.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipient {
    
    private String email;
    private String name;
}
