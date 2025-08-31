package com.hms.appointment.service;


import com.hms.appointment.dto.PrescriptionDTO;
import com.hms.appointment.exception.HmsException;
import com.hms.appointment.repository.PrescriptionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PrescriptionServiceImpl implements PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

private final MedicineService medicineService;
    @Override
    public Long savePrescription(PrescriptionDTO Request) {
Request.setPrescriptionDate(LocalDate.now());
   Long prescriptionId= prescriptionRepository.save(Request.toEntity()).getId();
Request.getMedicines().forEach(medicine -> {
    medicine.setPrescriptionId(prescriptionId);
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
}
