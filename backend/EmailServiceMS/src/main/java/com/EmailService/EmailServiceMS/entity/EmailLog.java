package com.EmailService.EmailServiceMS.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String recipient;

    @Column(length = 255)
    private String subject;

    private String emailType;

    private String status;

    @Column(length = 1000)
    private String error;

    private LocalDateTime createdAt;

    @PrePersist
    protected void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
