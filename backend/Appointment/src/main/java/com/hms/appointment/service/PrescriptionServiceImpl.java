package com.hms.appointment.service;


import com.hms.appointment.client.ProfileClient;
import com.hms.appointment.dto.DoctorDTO;
import com.hms.appointment.dto.DoctorName;
import com.hms.appointment.dto.MedicineDTO;
import com.hms.appointment.dto.PatientDTO;
import com.hms.appointment.dto.PrescriptionDTO;
import com.hms.appointment.dto.PrescriptionDetails;
import com.hms.appointment.dto.event.PrescriptionCreatedEvent;
import com.hms.appointment.entity.Prescription;
import com.hms.appointment.exception.HmsException;
import com.hms.appointment.kafka.AppointmentEventPublisher;
import com.hms.appointment.repository.PrescriptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionServiceImpl implements PrescriptionService {
    private static final Logger log = LoggerFactory.getLogger(PrescriptionServiceImpl.class);

    private final PrescriptionRepository prescriptionRepository;

private final MedicineService medicineService;
    private final ProfileClient profileClient;
private final AppointmentEventPublisher appointmentEventPublisher;

@Override
public Long savePrescription(PrescriptionDTO Request) {
    Request.setPrescriptionDate(LocalDate.now());
    Long prescriptionId = prescriptionRepository.save(Request.toEntity()).getId();
    if (Request.getMedicines() != null && !Request.getMedicines().isEmpty()) {
        Request.getMedicines().forEach(medicine -> medicine.setPrescriptionId(prescriptionId));
        medicineService.saveAllMedicines(Request.getMedicines());
    }
    publishPrescriptionCreated(prescriptionId, Request);
    return prescriptionId;
}

private void publishPrescriptionCreated(Long prescriptionId, PrescriptionDTO request) {
    try {
        PatientDTO patientDTO = profileClient.getPatientById(request.getPatientId());
        DoctorDTO doctorDTO = profileClient.getDoctorById(request.getDoctorId());
        if (patientDTO == null || patientDTO.getEmail() == null) {
            return;
        }
        List<String> medicineSummaries = request.getMedicines() == null ? List.of()
                : request.getMedicines().stream()
                .map(m -> {
                    StringBuilder sb = new StringBuilder(m.getName());
                    if (m.getDosage() != null) sb.append(" - ").append(m.getDosage());
                    if (m.getFrequency() != null) sb.append(", ").append(m.getFrequency());
                    if (m.getDuration() != null) sb.append(", ").append(m.getDuration()).append(" day(s)");
                    return sb.toString();
                })
                .toList();

        appointmentEventPublisher.publishPrescriptionCreated(new PrescriptionCreatedEvent(
                prescriptionId,
                request.getAppointmentId(),
                request.getPatientId(),
                patientDTO.getName(),
                patientDTO.getEmail(),
                request.getDoctorId(),
                doctorDTO != null ? doctorDTO.getName() : null,
                request.getPrescriptionDate(),
                request.getNotes(),
                medicineSummaries
        ));
    } catch (Exception e) {
        log.error("Failed to publish prescription-created event for prescriptionId={}: {}", prescriptionId, e.getMessage(), e);
    }
}

    @Override
    public PrescriptionDTO getPrescriptionByAppointmentId(Long appointmentId) throws  HmsException {
         PrescriptionDTO prescriptionDTO = prescriptionRepository.findFirstByAppointment_IdOrderByIdDesc(appointmentId)
                 .orElseThrow(()-> new HmsException("PRESCRIPTION_NOT_FOUND"))
                 .toDTO();


         prescriptionDTO.setMedicines(medicineService.getAllMedicinesByPrescriptionId(prescriptionDTO.getId()));

         return prescriptionDTO;
    }

    @Override
    public PrescriptionDTO getPrescriptionById(Long prescriptionId) throws HmsException{
        PrescriptionDTO dto = prescriptionRepository.findById(prescriptionId).orElseThrow(()->new HmsException("PRESCRIPTION_NOT_FOUND")).toDTO();
        dto.setMedicines(medicineService.getAllMedicinesByPrescriptionId(dto.getId()));
        return dto;
    }

    @Override
    public List<PrescriptionDetails> getPrescriptionsByPatientId(Long patientId) throws HmsException {
        List<Prescription> prescriptions = prescriptionRepository.findAllByPatientId(patientId);
        if(prescriptions.isEmpty()){
            throw new HmsException("PRESCRIPTIONS_NOT_FOUND" );

        }
        List<PrescriptionDetails> prescriptionDetails = prescriptions.stream()
                .map(Prescription::toDetails)
                .toList();
        prescriptionDetails.forEach(details->{
            details.setMedicines(medicineService.getAllMedicinesByPrescriptionId(details.getId()));

        });
        List<Long> doctorIds = prescriptionDetails.stream()
                .map(PrescriptionDetails::getDoctorId)
                .distinct()
                .toList();
        List<DoctorName>doctorNames=profileClient.getDoctorsById(doctorIds);
        Map<Long,String> doctorMap = doctorNames.stream()
                .collect(java.util.stream.Collectors.toMap(DoctorName::getId, DoctorName::getName));

        prescriptionDetails.forEach(details->{
            String doctorName = doctorMap.get(details.getDoctorId());
            if(doctorName != null){
                details.setDoctorName(doctorName);
            }else {
                details.setDoctorName("Unknown Doctor");
            }
        });
        return prescriptionDetails;
    }

    @Override
    public List<PrescriptionDetails> getPrescriptions() throws HmsException {
        List<Prescription> prescriptions = (List<Prescription>) prescriptionRepository.findAll();
        List<PrescriptionDetails> prescriptionDetails = prescriptions.stream()
                .map(Prescription::toDetails)
                .toList();

        List<Long> doctorIds = prescriptionDetails.stream()
                .map(PrescriptionDetails::getDoctorId)
                .distinct()
                .toList();

        List<Long> patientIds = prescriptionDetails.stream().map(PrescriptionDetails::getPatientId).distinct().toList();
        List<DoctorName>doctorNames=profileClient.getDoctorsById(doctorIds);
        List<DoctorName>patientNames=profileClient.getPatientsById(patientIds);

        Map<Long,String> doctorMap = doctorNames.stream()
                .collect(java.util.stream.Collectors.toMap(DoctorName::getId, DoctorName::getName));

        Map<Long,String> patientMap = patientNames.stream()
                .collect(java.util.stream.Collectors.toMap(DoctorName::getId, DoctorName::getName));

        prescriptionDetails.forEach(details->{
            String doctorName = doctorMap.get(details.getDoctorId());
            String patientName = patientMap.get(details.getPatientId());
            if(doctorName != null){
                details.setDoctorName(doctorName);
            }else {
                details.setDoctorName("Unknown Doctor");
            }
            if(patientName != null){
                details.setPatientName(patientName);

            }else {
                details.setPatientName("Unknown Patient");
            }
        });
        return prescriptionDetails;
    }

    @Override
    public List<MedicineDTO> getMedicineByPatientId(Long patientId) throws HmsException {

        List<Long>Pids = prescriptionRepository.findAllPrescriptionIdByPatientId(patientId);

        return medicineService.getMedicinesByPrescriptionIds(Pids);

    }
}
