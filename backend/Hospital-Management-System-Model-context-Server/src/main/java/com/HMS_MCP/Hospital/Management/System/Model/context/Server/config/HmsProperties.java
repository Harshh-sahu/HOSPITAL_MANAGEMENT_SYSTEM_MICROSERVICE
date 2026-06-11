package com.HMS_MCP.Hospital.Management.System.Model.context.Server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "hms")
@Data
public class HmsProperties {

    private String userServiceUrl = "http://localhost:8080";
    private String profileServiceUrl = "http://localhost:9100";
    private String appointmentServiceUrl = "http://localhost:9200";
    private String pharmacyServiceUrl = "http://localhost:9300";
    private String authToken = "";
    private String secretKey = "SECRET";
}
