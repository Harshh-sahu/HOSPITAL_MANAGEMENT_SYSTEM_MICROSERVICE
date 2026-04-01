package com.hms.appointment.service;

import com.hms.appointment.dto.MedicineDTO;
import com.hms.appointment.entity.Medicine;
import com.hms.appointment.entity.Prescription;
import com.hms.appointment.repository.MedicineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicineServiceImplTest {

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private MedicineServiceImpl medicineService;

    @Test
    void saveMedicine_returnsId() {
        MedicineDTO request = new MedicineDTO(null, "Paracetamol", 101L, "500mg", "TID", 5, "Oral", "Tablet", "After food", 1L);
        when(medicineRepository.save(any(Medicine.class))).thenReturn(new Medicine(11L, "Paracetamol", 101L, "500mg", "TID", 5, "Oral", "Tablet", "After food", new Prescription(1L)));

        Long id = medicineService.saveMedicine(request);

        assertEquals(11L, id);
        verify(medicineRepository).save(any(Medicine.class));
    }

    @Test
    void getAllMedicinesByPrescriptionId_mapsToDto() {
        when(medicineRepository.findAllByPrescription_Id(1L)).thenReturn(List.of(
                new Medicine(1L, "Paracetamol", 101L, "500mg", "TID", 5, "Oral", "Tablet", "After food", new Prescription(1L))
        ));

        List<MedicineDTO> result = medicineService.getAllMedicinesByPrescriptionId(1L);

        assertEquals(1, result.size());
        assertEquals("Paracetamol", result.get(0).getName());
        assertEquals(1L, result.get(0).getPrescriptionId());
    }

    @Test
    void getMedicinesByPrescriptionIds_mapsToDto() {
        when(medicineRepository.findAllByPrescription_IdIn(List.of(1L, 2L))).thenReturn(List.of(
                new Medicine(2L, "Cetirizine", 202L, "10mg", "OD", 3, "Oral", "Tablet", "Night", new Prescription(2L))
        ));

        List<MedicineDTO> result = medicineService.getMedicinesByPrescriptionIds(List.of(1L, 2L));

        assertEquals(1, result.size());
        assertEquals("Cetirizine", result.get(0).getName());
        assertEquals(2L, result.get(0).getPrescriptionId());
    }
}

