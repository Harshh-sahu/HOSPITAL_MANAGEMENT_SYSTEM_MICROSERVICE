package com.HMS_MCP.Hospital.Management.System.Model.context.Server.config;

import com.HMS_MCP.Hospital.Management.System.Model.context.Server.tools.*;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class McpToolsConfig {

    @Bean
    public RestClient restClient(HmsProperties hmsProperties) {
        return RestClient.builder()
                .defaultHeader("X-Secret-Key", hmsProperties.getSecretKey())
                .build();
    }

    @Bean
    public ToolCallbackProvider hmsToolCallbackProvider(
            UserTools userTools,
            DoctorTools doctorTools,
            PatientTools patientTools,
            AppointmentTools appointmentTools,
            PharmacyTools pharmacyTools) {
        return MethodToolCallbackProvider.builder()
                .toolObjects(userTools, doctorTools, patientTools, appointmentTools, pharmacyTools)
                .build();
    }
}
