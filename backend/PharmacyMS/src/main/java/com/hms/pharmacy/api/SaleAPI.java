package com.hms.pharmacy.api;

import com.hms.pharmacy.dto.ResponseDTO;
import com.hms.pharmacy.dto.SaleDTO;
import com.hms.pharmacy.dto.SaleItemDTO;
import com.hms.pharmacy.dto.SaleRequest;
import com.hms.pharmacy.exception.HmsException;
import com.hms.pharmacy.service.SaleItemService;
import com.hms.pharmacy.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/pharmacy/sales")
@RequiredArgsConstructor
public class SaleAPI {

    private final SaleService saleService;
    private final SaleItemService saleItemService;

    @PostMapping("/create")
    public ResponseEntity<Long> createSaleItem(@RequestBody SaleRequest saleDTO) throws HmsException {
        return new ResponseEntity<>(saleService.createSale(saleDTO), org.springframework.http.HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDTO> updateSale(@RequestBody SaleDTO dto) throws HmsException {
        saleService.updateSale(dto);
        return new ResponseEntity<>(new ResponseDTO("Sale Update SuccessFully"), HttpStatus.OK);
    }


    @GetMapping("getSaleItem/{saleId}")
    public ResponseEntity<List<SaleItemDTO>> getSaleItems(@PathVariable Long saleId) throws HmsException {
        List<SaleItemDTO> saleItems = saleItemService.getSaleItemsBySaleId(saleId);
        return new ResponseEntity<>(saleItems, HttpStatus.OK);

    }

    @GetMapping("/get/{id}")
    public ResponseEntity<SaleDTO> getSale(@PathVariable Long id) throws HmsException {
        SaleDTO saleDTO = saleService.getSale(id);
        return new ResponseEntity<>(saleDTO, HttpStatus.OK);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<SaleDTO>> getAllSales() throws HmsException{
        List<SaleDTO> sales = saleService.getAllSales();
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }
}

