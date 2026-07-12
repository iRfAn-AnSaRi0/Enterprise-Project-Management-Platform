package com.example.enterprise_project_management_platform.service.implement;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.enterprise_project_management_platform.client.BrevoClient;
import com.example.enterprise_project_management_platform.config.BrevoConfig;
import com.example.enterprise_project_management_platform.dto.email.BrevoEmailRequest;
import com.example.enterprise_project_management_platform.dto.email.Recipient;
import com.example.enterprise_project_management_platform.dto.email.Sender;
import com.example.enterprise_project_management_platform.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final BrevoClient brevoClient;
    private final BrevoConfig brevoConfig;

    private void sendEmail(String toEmail,
            String userName,
            String subject,
            String htmlContent) {

        Sender sender = new Sender(
                brevoConfig.getSenderName(),
                brevoConfig.getSenderEmail());

        Recipient recipient = new Recipient(
                toEmail,
                userName);

        BrevoEmailRequest request = new BrevoEmailRequest();
        request.setSender(sender);
        request.setTo(List.of(recipient));
        request.setSubject(subject);
        request.setHtmlContent(htmlContent);

        brevoClient.sendEmail(request);

    }

    @Override
    public void sendVerificationEmail(String toEmail, String userName, String token) {

        String verificationLink = "http://localhost:8080/api/auth/verify-email?token=" + token;

        String subject = "Verify Your Email";

        String htmlContent = """
                <html>
                <body>
                    <h2>Welcome to Enterprise Project Management Platform</h2>

                    <p>Hello %s,</p>

                    <p>Thank you for registering.</p>

                    <p>Please click the button below to verify your email.</p>

                    <a href="%s"
                       style="
                            background:#2563eb;
                            color:white;
                            padding:12px 20px;
                            text-decoration:none;
                            border-radius:5px;">
                        Verify Email
                    </a>

                    <p>This link will expire in 24 hours.</p>

                    <p>If you didn't create this account, please ignore this email.</p>

                </body>
                </html>
                """.formatted(userName, verificationLink);

        sendEmail(toEmail, userName, subject, htmlContent);

    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String userName, String token) {
        String resetPasswordLink = "http://localhost:8080/api/auth/reset-password?token=" + token;

        String subject = "Reset Your Password";

        String htmlContent = """
                <html>
                <body>
                    <h2>Enterprise Project Management Platform</h2>

                    <p>Hello %s,</p>

                    <p>We received a request to reset your account password.</p>

                    <p>Please click the button below to create a new password.</p>

                    <a href="%s"
                       style="
                            background:#dc2626;
                            color:white;
                            padding:12px 20px;
                            text-decoration:none;
                            border-radius:5px;">
                        Reset Password
                    </a>

                    <p>This link will expire in 15 minutes.</p>

                    <p>If you didn't request a password reset, you can safely ignore this email. Your password will remain unchanged.</p>

                </body>
                </html>
                """
                .formatted(userName, resetPasswordLink);

                sendEmail(toEmail, userName, subject, htmlContent);

    }

}
