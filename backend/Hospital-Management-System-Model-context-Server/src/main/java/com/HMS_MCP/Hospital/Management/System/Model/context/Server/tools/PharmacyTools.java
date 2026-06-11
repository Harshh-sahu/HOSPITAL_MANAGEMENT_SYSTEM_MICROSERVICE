package com.HMS_MCP.Hospital.Management.System.Model.context.Server.tools;

import com.HMS_MCP.Hospital.Management.System.Model.context.Server.config.HmsProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PharmacyTools {

    private final RestClient restClient;
    private final HmsProperties hmsProperties;

    @Tool(description = "Add a new medicine to the pharmacy catalog. Returns the generated medicine ID. " +
            "category values: ANTIBIOTIC, ANALGESIC, ANTIPYRETIC, ANTIVIRAL, ANTIFUNGAL, VITAMIN, SUPPLEMENT, OTHER. " +
            "type values: TABLET, CAPSULE, SYRUP, INJECTION, CREAM, DROPS. " +
            "unitPrice and stock are integers.")
    public String addMedicine(String name, String dosage, String category, String type,
                              String manufacturer, Integer unitPrice, Integer stock) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("name", name);
            body.put("dosage", dosage);
            body.put("category", category);
            body.put("type", type);
            body.put("manufacturer", manufacturer);
            body.put("unitPrice", unitPrice);
            body.put("stock", stock);

            return restClient.post()
                    .uri(hmsProperties.getPharmacyServiceUrl() + "/pharmacy/medicines/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error adding medicine: " + e.getMessage();
        }
    }

    @Tool(description = "Get a medicine full details from the pharmacy catalog by medicine ID. " +
            "Returns name, dosage, category, type, manufacturer, unit price, and current stock.")
    public String getMedicineById(Long medicineId) {
        try {
            return restClient.get()
                    .uri(hmsProperties.getPharmacyServiceUrl() + "/pharmacy/medicines/get/" + medicineId)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching medicine: " + e.getMessage();
        }
    }

    @Tool(description = "Get all medicines available in the pharmacy catalog. Returns the complete inventory including stock levels and pricing for all medicines.")
    public String getAllMedicines() {
        try {
            return restClient.get()
                    .uri(hmsProperties.getPharmacyServiceUrl() + "/pharmacy/medicines/getAll")
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching all medicines: " + e.getMessage();
        }
    }

    @Tool(description = "Update an existing medicine in the pharmacy catalog by medicine ID. Returns Medicine Updated on success.")
    public String updateMedicine(Long medicineId, String name, String dosage, String category,
                                 String type, String manufacturer, Integer unitPrice, Integer stock) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("id", medicineId);
            body.put("name", name);
            body.put("dosage", dosage);
            body.put("category", category);
            body.put("type", type);
            body.put("manufacturer", manufacturer);
            body.put("unitPrice", unitPrice);
            body.put("stock", stock);

            return restClient.put()
                    .uri(hmsProperties.getPharmacyServiceUrl() + "/pharmacy/medicines/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error updating medicine: " + e.getMessage();
        }
    }

    @Tool(description = "Create a pharmacy sale for medicine purchases. " +
            "prescriptionId links the sale to a prescription (pass 0 if there is no prescription). " +
            "buyerName and buyerContact are required. " +
            "saleItemsJson is a JSON array e.g. [{medicineId:1,quantity:2},{medicineId:3,quantity:1}]. " +
            "Returns the generated sale ID.")
    public String createSale(Long prescriptionId, String buyerName, String buyerContact, String saleItemsJson) {
        try {
            Map<String, Object> saleHeader = new HashMap<>();
            if (prescriptionId != null && prescriptionId > 0) {
                saleHeader.put("prescriptionId", prescriptionId);
            }
            saleHeader.put("buyerName", buyerName);
            saleHeader.put("buyerContact", buyerContact);

            Map<String, Object> body = new HashMap<>();
            body.put("sale", saleHeader);
            body.put("saleItems", saleItemsJson);

            return restClient.post()
                    .uri(hmsProperties.getPharmacyServiceUrl() + "/pharmacy/sales/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error creating sale: " + e.getMessage();
        }
    }

    @Tool(description = "Get a pharmacy sale record by sale ID. Returns sale details including buyer information, total amount, and linked prescription ID.")
    public String getSaleById(Long saleId) {
        try {
            return restClient.get()
                    .uri(hmsProperties.getPharmacyServiceUrl() + "/pharmacy/sales/get/" + saleId)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching sale: " + e.getMessage();
        }
    }

    @Tool(description = "Get all pharmacy sales records. Returns the complete sales history with buyer details, amounts, and linked prescriptions.")
    public String getAllSales() {
        try {
            return restClient.get()
                    .uri(hmsProperties.getPharmacyServiceUrl() + "/pharmacy/sales/getAll")
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching all sales: " + e.getMessage();
        }
    }

    @Tool(description = "Get all individual medicine line items for a specific sale by sale ID. Returns each medicine and quantity in the sale.")
    public String getSaleItems(Long saleId) {
        try {
            return restClient.get()
                    .uri(hmsProperties.getPharmacyServiceUrl() + "/pharmacy/sales/getSaleItem/" + saleId)
                    .retrieve()
                    .body(String.class);
        } catch (Exception e) {
            return "Error fetching sale items: " + e.getMessage();
        }
    }
}
