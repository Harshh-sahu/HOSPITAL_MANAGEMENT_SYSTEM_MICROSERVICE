package com.HMS_MCP.Hospital.Management.System.Model.context.Server.controller;

import com.HMS_MCP.Hospital.Management.System.Model.context.Server.dto.ChatRequest;
import com.HMS_MCP.Hospital.Management.System.Model.context.Server.dto.ChatResponse;
import com.HMS_MCP.Hospital.Management.System.Model.context.Server.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chatbot/message")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(
                chatService.chat(
                        request.getMessage(),
                        request.getConversationId(),
                        request.getRole(),
                        request.getUserName(),
                        request.getProfileId()
                ));
    }

    @DeleteMapping("/chatbot/history/{conversationId}")
    public ResponseEntity<Void> clearHistory(@PathVariable String conversationId) {
        chatService.clearHistory(conversationId);
        return ResponseEntity.noContent().build();
    }
}
