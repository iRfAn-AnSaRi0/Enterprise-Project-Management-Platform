package com.example.enterprise_project_management_platform.dto.email;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrevoEmailRequest {
    
    private Sender sender;
    private List<Recipient> to;
    private String subject;
    private String htmlContent;
}
