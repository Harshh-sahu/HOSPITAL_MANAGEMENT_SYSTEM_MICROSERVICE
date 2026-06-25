package com.HMS_MCP.Hospital.Management.System.Model.context.Server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatResponse {
    private String response;
    private String conversationId;
    private LocalDateTime timestamp;
}
