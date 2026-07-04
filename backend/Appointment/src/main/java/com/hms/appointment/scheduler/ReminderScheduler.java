package com.hms.appointment.scheduler;

import com.hms.appointment.client.ProfileClient;
import com.hms.appointment.dto.PatientDTO;
import com.hms.appointment.dto.DoctorDTO;
import com.hms.appointment.dto.Status;
import com.hms.appointment.dto.event.AppointmentReminderEvent;
import com.hms.appointment.dto.event.FollowUpReminderEvent;
import com.hms.appointment.entity.Appointment;
import com.hms.appointment.entity.ApRecord;
import com.hms.appointment.kafka.AppointmentEventPublisher;
import com.hms.appointment.repository.ApRecordRepository;
import com.hms.appointment.repository.AppointmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);

    @Autowired private AppointmentRepository appointmentRepository;
    @Autowired private ApRecordRepository apRecordRepository;
    @Autowired private AppointmentEventPublisher appointmentEventPublisher;
    @Autowired private ProfileClient profileClient;

    @Scheduled(cron = "0 0 * * * *")
    public void sendAppointmentReminders() {
        LocalDateTime windowStart = LocalDateTime.now().plusHours(23);
        LocalDateTime windowEnd   = LocalDateTime.now().plusHours(25);

        List<Appointment> upcoming = appointmentRepository
                .findByAppointmentTimeBetweenAndReminderSentFalseAndStatus(windowStart, windowEnd, Status.SCHEDULED);

        log.info("Reminder scheduler: found {} appointments in 24h window", upcoming.size());

        for (Appointment appointment : upcoming) {
            try {
                PatientDTO patient = profileClient.getPatientById(appointment.getPatientId());
                DoctorDTO  doctor  = profileClient.getDoctorById(appointment.getDoctorId());

                if (patient == null || patient.getEmail() == null) {
                    log.warn("Skipping reminder for appointmentId={}: no patient email", appointment.getId());
                    continue;
                }

                AppointmentReminderEvent event = new AppointmentReminderEvent(
                        appointment.getId(),
                        appointment.getPatientId(),
                        patient.getName(),
                        patient.getEmail(),
                        appointment.getDoctorId(),
                        doctor != null ? doctor.getName() : null,
                        doctor != null ? doctor.getEmail() : null,
                        appointment.getAppointmentTime(),
                        appointment.getReason()
                );
                appointmentEventPublisher.publishAppointmentReminder(event);

                appointment.setReminderSent(true);
                appointmentRepository.save(appointment);
            } catch (Exception e) {
                log.error("Failed to send reminder for appointmentId={}: {}", appointment.getId(), e.getMessage(), e);
            }
        }
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void sendFollowUpReminders() {
        LocalDate today = LocalDate.now();
        List<ApRecord> records = apRecordRepository.findByFollowUpDate(today);

        log.info("Follow-up reminder scheduler: found {} records for {}", records.size(), today);

        for (ApRecord record : records) {
            try {
                PatientDTO patient = profileClient.getPatientById(record.getPatientId());
                DoctorDTO  doctor  = profileClient.getDoctorById(record.getDoctorId());

                if (patient == null || patient.getEmail() == null) {
                    log.warn("Skipping follow-up reminder for recordId={}: no patient email", record.getId());
                    continue;
                }

                FollowUpReminderEvent event = new FollowUpReminderEvent(
                        record.getId(),
                        record.getPatientId(),
                        patient.getName(),
                        patient.getEmail(),
                        record.getDoctorId(),
                        doctor != null ? doctor.getName() : null,
                        today,
                        record.getDiagnosis()
                );
                appointmentEventPublisher.publishFollowUpReminder(event);
            } catch (Exception e) {
                log.error("Failed to send follow-up reminder for recordId={}: {}", record.getId(), e.getMessage(), e);
            }
        }
    }
}
