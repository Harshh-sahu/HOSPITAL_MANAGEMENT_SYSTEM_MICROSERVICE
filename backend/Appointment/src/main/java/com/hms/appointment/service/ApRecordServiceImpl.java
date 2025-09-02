package com.hms.appointment.service;

import com.hms.appointment.client.ProfileClient;
import com.hms.appointment.dto.ApRecordDTO;
import com.hms.appointment.dto.DoctorDTO;
import com.hms.appointment.dto.DoctorName;
import com.hms.appointment.dto.RecordDetails;
import com.hms.appointment.entity.ApRecord;
import com.hms.appointment.exception.HmsException;
import com.hms.appointment.repository.ApRecordRepository;
import com.hms.appointment.utility.StringListConverter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ApRecordServiceImpl implements ApRecordService{
private final PrescriptionService prescriptionService;
    private final ApRecordRepository apRecordRepository;
    private final ProfileClient profileClient;
    @Override
    public Long createApRecord(ApRecordDTO request) throws HmsException {
        Optional<ApRecord> existingRecord = apRecordRepository.findByAppointment_Id(request.getAppointmentId());
        if(existingRecord.isPresent()){
            throw new HmsException("Appointment record already exists for appointment ID: " + request.getAppointmentId());
        }
        request.setCreatedAt(LocalDateTime.now());
        System.out.println(request+"  in service");
        Long id= apRecordRepository.save(request.toEntity()).getId();
        if(request.getPrescription()!=null){
            request.getPrescription().setAppointmentId(request.getAppointmentId());
            prescriptionService.savePrescription(request.getPrescription());
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
   return apRecordRepository.findByAppointment_Id(appointmentId)
           .orElseThrow(()-> new HmsException("APPOINTMENT_RECORD_NOT_FOUND"))
           .toDTO();
    }

    @Override
    public ApRecordDTO getApRecordDetailsByAppointmentId(Long appointmentId) throws HmsException {
        ApRecordDTO record= apRecordRepository.findByAppointment_Id(appointmentId)
                .orElseThrow(()-> new HmsException("APPOINTMENT_RECORD_NOT_FOUND"))
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
