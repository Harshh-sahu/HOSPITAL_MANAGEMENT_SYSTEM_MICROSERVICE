package com.hms.appointment.service;


import com.hms.appointment.client.ProfileClient;
import com.hms.appointment.dto.DoctorName;
import com.hms.appointment.dto.PrescriptionDTO;
import com.hms.appointment.dto.PrescriptionDetails;
import com.hms.appointment.entity.Prescription;
import com.hms.appointment.exception.HmsException;
import com.hms.appointment.repository.PrescriptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

private final MedicineService medicineService;
    private final ProfileClient profileClient;

    @Override
    public Long savePrescription(PrescriptionDTO Request) {
Request.setPrescriptionDate(LocalDate.now());
   Long prescriptionId= prescriptionRepository.save(Request.toEntity()).getId();
Request.getMedicines().forEach(medicine -> {
    medicine.setPrescriptionId(prescriptionId);
    System.out.println(medicine);
});

medicineService.saveAllMedicines(Request.getMedicines());

   return prescriptionId;
    }

    @Override
    public PrescriptionDTO getPrescriptionByAppointmentId(Long appointmentId) throws  HmsException {
         PrescriptionDTO prescriptionDTO = prescriptionRepository.findByAppointment_Id(appointmentId)
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
}
