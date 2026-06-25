package com.HMS_MCP.Hospital.Management.System.Model.context.Server.dto;

import lombok.Data;

@Data
public class ChatRequest {
    private String message;
    private String conversationId;
}
