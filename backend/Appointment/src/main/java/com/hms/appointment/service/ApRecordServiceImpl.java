package com.hms.appointment.service;

import com.hms.appointment.client.ProfileClient;
import com.hms.appointment.dto.ApRecordDTO;
import com.hms.appointment.dto.DoctorDTO;
import com.hms.appointment.dto.DoctorName;
import com.hms.appointment.dto.PatientDTO;
import com.hms.appointment.dto.RecordDetails;
import com.hms.appointment.dto.event.ReportCreatedEvent;
import com.hms.appointment.entity.ApRecord;
import com.hms.appointment.exception.HmsException;
import com.hms.appointment.kafka.AppointmentEventPublisher;
import com.hms.appointment.repository.ApRecordRepository;
import com.hms.appointment.utility.StringListConverter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ApRecordServiceImpl implements ApRecordService{

    private static final Logger log = LoggerFactory.getLogger(ApRecordServiceImpl.class);

    private final PrescriptionService prescriptionService;
    private final ApRecordRepository apRecordRepository;
    private final ProfileClient profileClient;
    private final AppointmentEventPublisher appointmentEventPublisher;
    @Override
    public Long createApRecord(ApRecordDTO request) throws HmsException {
        request.setCreatedAt(LocalDateTime.now());
        Long id = apRecordRepository.save(request.toEntity()).getId();
        if (request.getPrescription() != null) {
            request.getPrescription().setAppointmentId(request.getAppointmentId());
            prescriptionService.savePrescription(request.getPrescription());
        }
        try {
            PatientDTO patient = profileClient.getPatientById(request.getPatientId());
            DoctorDTO doctor   = profileClient.getDoctorById(request.getDoctorId());
            if (patient != null && patient.getEmail() != null) {
                List<ReportCreatedEvent.MedicineInfo> medicines = null;
                if (request.getPrescription() != null && request.getPrescription().getMedicines() != null) {
                    medicines = request.getPrescription().getMedicines().stream()
                        .map(m -> new ReportCreatedEvent.MedicineInfo(
                            m.getName(), m.getDosage(), m.getFrequency(),
                            m.getDuration(), m.getType(), m.getInstructions()))
                        .toList();
                }
                appointmentEventPublisher.publishReportCreated(new ReportCreatedEvent(
                    id,
                    request.getAppointmentId(),
                    request.getPatientId(), patient.getName(), patient.getEmail(),
                    request.getDoctorId(), doctor != null ? doctor.getName() : null,
                    request.getSymptoms(), request.getDiagnosis(),
                    request.getTests(), request.getNotes(), request.getReferral(),
                    request.getFollowUpDate(), request.getCreatedAt(),
                    medicines
                ));
            }
        } catch (Exception e) {
            log.error("Failed to publish report-created event for recordId={}: {}", id, e.getMessage(), e);
        }
        return id;
    }

    @Override
    public void updateApRecord(ApRecordDTO request) throws HmsException {
        ApRecord existingRecord = apRecordRepository.findById(request.getId()).orElseThrow(()-> new HmsException("APPOINTMENT_RECORD_NOT_FOUND"));
existingRecord.setNotes(request.getNotes());
existingRecord.setDiagnosis(request.getDiagnosis());
existingRecord.setFollowUpDate(request.getFollowUpDate());
existingRecord.setSymptoms(StringListConverter.convertListToString(request.getSymptoms()));
existingRecord.setTests(StringListConverter.convertListToString(request.getTests()));
existingRecord.setReferral(request.getReferral());

apRecordRepository.save(existingRecord);
    }

    @Override
    public ApRecordDTO getApRecordByAppointmentId(Long appointmentId) throws HmsException {
        return apRecordRepository.findFirstByAppointment_IdOrderByIdDesc(appointmentId)
                .orElseThrow(() -> new HmsException("APPOINTMENT_RECORD_NOT_FOUND"))
                .toDTO();
    }

    @Override
    public ApRecordDTO getApRecordDetailsByAppointmentId(Long appointmentId) throws HmsException {
        ApRecordDTO record = apRecordRepository.findFirstByAppointment_IdOrderByIdDesc(appointmentId)
                .orElseThrow(() -> new HmsException("APPOINTMENT_RECORD_NOT_FOUND"))
                .toDTO();
      record.setPrescription(prescriptionService.getPrescriptionByAppointmentId(appointmentId));

      return record;
    }

    @Override
    public ApRecordDTO getApRecordById(Long id) throws HmsException {
        return apRecordRepository.findById(id)
                .orElseThrow(()-> new HmsException("APPOINTMENT_RECORD_NOT_FOUND"))
                .toDTO();
    }

    @Override
    public List<RecordDetails> getRecordByPatientId(Long patientId) throws HmsException {

List<ApRecord> records = apRecordRepository.findByPatientId(patientId);

List<RecordDetails> recordDetails= records.stream()
        .map(ApRecord::toRecordDetails)
        .toList();
        List<Long >doctorIds = recordDetails.stream()
                .map(RecordDetails::getDoctorId)
                .distinct()
                .toList();

List<DoctorName> doctors= profileClient.getDoctorsById(doctorIds);
        Map<Long,String >doctorMap = doctors.stream()
                .collect(Collectors.toMap(DoctorName::getId,DoctorName::getName));

        recordDetails.forEach(record-> {
            String doctorName = doctorMap.get(record.getDoctorId());
            if(doctorName != null){
                record.setDoctorName(doctorName);

            }else {
                record.setDoctorName("Unknown Doctor");
            }
        });
        return recordDetails;
    }

    @Override
    public Boolean isRecordExists(Long appointmentId) throws HmsException {
        return apRecordRepository.existsByAppointment_Id(appointmentId);
    }

}
