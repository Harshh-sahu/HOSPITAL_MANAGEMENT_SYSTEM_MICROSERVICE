package com.EmailService.EmailServiceMS.repository;

import com.EmailService.EmailServiceMS.entity.EmailLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailLogRepository extends JpaRepository<EmailLog, Long> {
}
