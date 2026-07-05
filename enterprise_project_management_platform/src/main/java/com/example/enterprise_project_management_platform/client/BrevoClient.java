package com.example.enterprise_project_management_platform.client;

import org.springframework.stereotype.Component;

import com.example.enterprise_project_management_platform.config.BrevoConfig;
import com.example.enterprise_project_management_platform.dto.email.BrevoEmailRequest;

import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BrevoClient {
    
    private final BrevoConfig brevoConfig;
    private final RestClient restClient;

    public void sendEmail(BrevoEmailRequest brevoEmailRequest){
       var response =  restClient.post()
         .uri("https://api.brevo.com/v3/smtp/email")
         .header("accept", "application/json")
         .header("api-key", brevoConfig.getApiKey())
         .header("content-type", "application/json")
         .body(brevoEmailRequest)
         .retrieve()
         .toBodilessEntity();

         System.out.println("Status Code: " + response.getStatusCode());
    }

}
