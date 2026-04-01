package com.hms.pharmacy.service;

import com.hms.pharmacy.dto.MedicineDTO;
import com.hms.pharmacy.entity.Medicine;
import com.hms.pharmacy.entity.MedicineCategory;
import com.hms.pharmacy.entity.MedicineType;
import com.hms.pharmacy.exception.HmsException;
import com.hms.pharmacy.repository.MedicineInventoryRepository;
import com.hms.pharmacy.repository.MedicineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicineServiceImplTest {

    @Mock
    private MedicineInventoryRepository medicineInventoryRepository;

    @Mock
    private MedicineRepository medicineRepository;

    @InjectMocks
    private MedicineServiceImpl medicineService;

    private MedicineDTO sampleDto(Long id, String name, String dosage) {
        return new MedicineDTO(id, name, dosage, MedicineCategory.ANALGESIC, MedicineType.TABLET, "ABC", 10, 20, LocalDateTime.now());
    }

    @Test
    void addMedicine_whenDuplicate_throws() {
        MedicineDTO dto = sampleDto(null, "Paracetamol", "500mg");
        when(medicineRepository.findByNameIgnoreCaseAndDosageIgnoreCase("Paracetamol", "500mg"))
                .thenReturn(Optional.of(new Medicine(1L, "Paracetamol", "500mg", MedicineCategory.ANALGESIC, MedicineType.TABLET, "ABC", 10, 0, LocalDateTime.now())));

        HmsException ex = assertThrows(HmsException.class, () -> medicineService.addMedicine(dto));

        assertEquals("MEDICINE_ALREADY_EXISTS", ex.getMessage());
    }

    @Test
    void addMedicine_success_setsDefaultsAndReturnsId() {
        MedicineDTO dto = sampleDto(null, "Paracetamol", "500mg");
        when(medicineRepository.findByNameIgnoreCaseAndDosageIgnoreCase("Paracetamol", "500mg")).thenReturn(Optional.empty());
        when(medicineRepository.save(any(Medicine.class)))
                .thenReturn(new Medicine(11L, "Paracetamol", "500mg", MedicineCategory.ANALGESIC, MedicineType.TABLET, "ABC", 10, 0, LocalDateTime.now()));

        Long id = medicineService.addMedicine(dto);

        assertEquals(11L, id);
        assertEquals(0, dto.getStock());
        assertNotNull(dto.getCreatedAt());
    }

    @Test
    void getMedicineById_notFound_throws() {
        when(medicineRepository.findById(99L)).thenReturn(Optional.empty());

        HmsException ex = assertThrows(HmsException.class, () -> medicineService.getMedicineById(99L));

        assertEquals("MEDICINE_NOT_FOUND", ex.getMessage());
    }

    @Test
    void addStock_updatesAndReturnsStock() {
        Medicine medicine = new Medicine(1L, "Paracetamol", "500mg", MedicineCategory.ANALGESIC, MedicineType.TABLET, "ABC", 10, 5, LocalDateTime.now());
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));

        Integer stock = medicineService.addStock(1L, 3);

        assertEquals(8, stock);
        verify(medicineRepository).save(medicine);
    }

    @Test
    void removeStock_updatesAndReturnsStock() {
        Medicine medicine = new Medicine(1L, "Paracetamol", "500mg", MedicineCategory.ANALGESIC, MedicineType.TABLET, "ABC", 10, 7, LocalDateTime.now());
        when(medicineRepository.findById(1L)).thenReturn(Optional.of(medicine));

        Integer stock = medicineService.removeStock(1L, 2);

        assertEquals(5, stock);
        verify(medicineRepository).save(medicine);
    }

    @Test
    void getAllMedicines_mapsToDtoList() {
        when(medicineRepository.findAll()).thenReturn(List.of(
                new Medicine(1L, "P", "500mg", MedicineCategory.ANALGESIC, MedicineType.TABLET, "ABC", 10, 1, LocalDateTime.now())
        ));

        List<MedicineDTO> list = medicineService.getAllMedicines();

        assertEquals(1, list.size());
        assertEquals("P", list.get(0).getName());
    }
}

