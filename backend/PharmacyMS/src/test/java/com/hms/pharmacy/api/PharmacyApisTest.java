package com.hms.pharmacy.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.pharmacy.dto.*;
import com.hms.pharmacy.entity.MedicineCategory;
import com.hms.pharmacy.entity.MedicineType;
import com.hms.pharmacy.entity.StockStatus;
import com.hms.pharmacy.service.MedicineInventoryService;
import com.hms.pharmacy.service.MedicineService;
import com.hms.pharmacy.service.SaleItemService;
import com.hms.pharmacy.service.SaleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({MedicineAPI.class, MedicineInventoryAPI.class, SaleAPI.class})
@AutoConfigureMockMvc(addFilters = false)
class PharmacyApisTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MedicineService medicineService;

    @MockBean
    private MedicineInventoryService medicineInventoryService;

    @MockBean
    private SaleService saleService;

    @MockBean
    private SaleItemService saleItemService;

    @Test
    void addMedicine_returns201() throws Exception {
        MedicineDTO dto = new MedicineDTO(null, "Paracetamol", "500mg", MedicineCategory.ANALGESIC, MedicineType.TABLET, "ABC", 10, 0, LocalDateTime.now());
        when(medicineService.addMedicine(any(MedicineDTO.class))).thenReturn(101L);

        mockMvc.perform(post("/pharmacy/medicines/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(101));
    }

    @Test
    void getAllMedicines_returns200() throws Exception {
        when(medicineService.getAllMedicines()).thenReturn(List.of(
                new MedicineDTO(1L, "Paracetamol", "500mg", MedicineCategory.ANALGESIC, MedicineType.TABLET, "ABC", 10, 20, LocalDateTime.now())
        ));

        mockMvc.perform(get("/pharmacy/medicines/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Paracetamol"));
    }

    @Test
    void addInventory_returns201() throws Exception {
        MedicineInventoryDTO dto = new MedicineInventoryDTO(null, 1L, "B1", 20, LocalDate.now().plusDays(10), LocalDate.now(), 20, StockStatus.ACTIVE);
        when(medicineInventoryService.addMedicine(any(MedicineInventoryDTO.class))).thenReturn(new MedicineInventoryDTO(77L, 1L, "B1", 20, LocalDate.now().plusDays(10), LocalDate.now(), 20, StockStatus.ACTIVE));

        mockMvc.perform(post("/pharmacy/inventory/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(77));
    }

    @Test
    void createSale_returns201() throws Exception {
        SaleRequest req = new SaleRequest(100L, "Buyer", "9999999999", 20.0,
                List.of(new SaleItemDTO(null, null, 1L, null, 2, 10.0)));
        when(saleService.createSale(any(SaleRequest.class))).thenReturn(5001L);

        mockMvc.perform(post("/pharmacy/sales/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").value(5001));
    }

    @Test
    void getAllSales_returns200() throws Exception {
        when(saleService.getAllSales()).thenReturn(List.of(new SaleDTO(1L, 100L, "Buyer", "999", LocalDateTime.now(), 20.0)));

        mockMvc.perform(get("/pharmacy/sales/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getMedicineById_returns200() throws Exception {
        when(medicineService.getMedicineById(1L)).thenReturn(new MedicineDTO(1L, "Paracetamol", "500mg", MedicineCategory.ANALGESIC, MedicineType.TABLET, "ABC", 10, 20, LocalDateTime.now()));

        mockMvc.perform(get("/pharmacy/medicines/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void updateMedicine_returns200() throws Exception {
        MedicineDTO dto = new MedicineDTO(1L, "Paracetamol", "500mg", MedicineCategory.ANALGESIC, MedicineType.TABLET, "ABC", 10, 20, LocalDateTime.now());

        mockMvc.perform(put("/pharmacy/medicines/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Medicine Updated !"));
    }

    @Test
    void updateInventory_returns200() throws Exception {
        MedicineInventoryDTO dto = new MedicineInventoryDTO(1L, 1L, "B1", 20, LocalDate.now().plusDays(10), LocalDate.now(), 20, StockStatus.ACTIVE);
        when(medicineInventoryService.updateMedicine(any(Long.class), any(MedicineInventoryDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/pharmacy/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getInventoryById_returns200() throws Exception {
        when(medicineInventoryService.getMedicineById(1L)).thenReturn(new MedicineInventoryDTO(1L, 1L, "B1", 20, LocalDate.now().plusDays(10), LocalDate.now(), 20, StockStatus.ACTIVE));

        mockMvc.perform(get("/pharmacy/inventory/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getAllInventory_returns200() throws Exception {
        when(medicineInventoryService.getAllMedicines()).thenReturn(List.of(new MedicineInventoryDTO(1L, 1L, "B1", 20, LocalDate.now().plusDays(10), LocalDate.now(), 20, StockStatus.ACTIVE)));

        mockMvc.perform(get("/pharmacy/inventory/getAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void updateSale_returns200() throws Exception {
        SaleDTO dto = new SaleDTO(1L, 100L, "Buyer", "999", LocalDateTime.now(), 20.0);

        mockMvc.perform(put("/pharmacy/sales/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Sale Update SuccessFully"));
    }

    @Test
    void getSaleItems_returns200() throws Exception {
        when(saleItemService.getSaleItemsBySaleId(1L)).thenReturn(List.of(new SaleItemDTO(1L, 1L, 1L, "B1", 2, 10.0)));

        mockMvc.perform(get("/pharmacy/sales/getSaleItem/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getSaleById_returns200() throws Exception {
        when(saleService.getSale(1L)).thenReturn(new SaleDTO(1L, 100L, "Buyer", "999", LocalDateTime.now(), 20.0));

        mockMvc.perform(get("/pharmacy/sales/get/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
