package br.edu.ifmg.hotelbao.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "EmailDTO", description = "Data Transfer Object representing the contents of an email message.")
public class EmailDTO {

    @Schema(
            description = "The recipient's email address.",
            example = "user@example.com"
    )
    @NotBlank(message = "Recipient email is required")
    @Email(message = "Invalid email format")
    private String to;

    @Schema(
            description = "Subject line of the email.",
            example = "Password Reset Request"
    )
    @NotBlank(message = "Email subject is required")
    private String subject;

    @Schema(
            description = "Body content of the email message.",
            example = "Click the link below to reset your password..."
    )
    @NotBlank(message = "Email body is required")
    private String body;

    public EmailDTO() {
    }

    public EmailDTO(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "EmailDTO{" +
                "to='" + to + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

}