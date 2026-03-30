package com.hms.pharmacy.api;

import com.hms.pharmacy.dto.ResponseDTO;
import com.hms.pharmacy.dto.SaleDTO;
import com.hms.pharmacy.dto.SaleItemDTO;
import com.hms.pharmacy.dto.SaleRequest;
import com.hms.pharmacy.exception.HmsException;
import com.hms.pharmacy.service.SaleItemService;
import com.hms.pharmacy.service.SaleService;
import com.hms.pharmacy.utility.ErrorInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/pharmacy/sales")
@RequiredArgsConstructor
@Validated
@Tag(name = "Pharmacy Sale APIs", description = "Sale and sale item operations")
@SecurityRequirement(name = "X-Secret-Key")
public class SaleAPI {

    private final SaleService saleService;
    private final SaleItemService saleItemService;

    @Operation(operationId = "createSaleItem", summary = "Create sale", description = "Creates sale header and line items")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sale created",
                    content = @Content(schema = @Schema(type = "integer", format = "int64", example = "7001"))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PostMapping("/create")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Sale request payload",
            content = @Content(schema = @Schema(implementation = SaleRequest.class)))
    public ResponseEntity<Long> createSaleItem(@Valid @RequestBody SaleRequest saleDTO) throws HmsException {
        return new ResponseEntity<>(saleService.createSale(saleDTO), org.springframework.http.HttpStatus.CREATED);
    }

    @Operation(operationId = "updateSale", summary = "Update sale", description = "Updates sale header details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sale updated",
                    content = @Content(schema = @Schema(implementation = ResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @PutMapping("/update")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, description = "Sale payload",
            content = @Content(schema = @Schema(implementation = SaleDTO.class)))
    public ResponseEntity<ResponseDTO> updateSale(@Valid @RequestBody SaleDTO dto) throws HmsException {
        saleService.updateSale(dto);
        return new ResponseEntity<>(new ResponseDTO("Sale Update SuccessFully"), HttpStatus.OK);
    }

    @Operation(operationId = "getSaleItems", summary = "Get sale items", description = "Returns all line items for a sale id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sale items fetched",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SaleItemDTO.class)))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })

    @GetMapping("getSaleItem/{saleId}")
    public ResponseEntity<List<SaleItemDTO>> getSaleItems(
            @Parameter(description = "Sale id", example = "7001") @PathVariable Long saleId
    ) throws HmsException {
        List<SaleItemDTO> saleItems = saleItemService.getSaleItemsBySaleId(saleId);
        return new ResponseEntity<>(saleItems, HttpStatus.OK);

    }

    @Operation(operationId = "getSale", summary = "Get sale by id", description = "Returns sale header details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sale fetched",
                    content = @Content(schema = @Schema(implementation = SaleDTO.class))),
            @ApiResponse(responseCode = "500", description = "Business or server error",
                    content = @Content(schema = @Schema(implementation = ErrorInfo.class)))
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<SaleDTO> getSale(
            @Parameter(description = "Sale id", example = "7001") @PathVariable Long id
    ) throws HmsException {
        SaleDTO saleDTO = saleService.getSale(id);
        return new ResponseEntity<>(saleDTO, HttpStatus.OK);
    }

    @Operation(operationId = "getAllSales", summary = "Get all sales", description = "Returns all sale headers")
    @ApiResponse(responseCode = "200", description = "Sales fetched",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = SaleDTO.class))))
    @GetMapping("/getAll")
    public ResponseEntity<List<SaleDTO>> getAllSales() throws HmsException{
        List<SaleDTO> sales = saleService.getAllSales();
        return new ResponseEntity<>(sales, HttpStatus.OK);
    }
}

