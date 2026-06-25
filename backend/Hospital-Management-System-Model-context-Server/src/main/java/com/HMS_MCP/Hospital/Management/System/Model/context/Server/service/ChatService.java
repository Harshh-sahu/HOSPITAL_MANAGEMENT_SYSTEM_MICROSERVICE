package com.HMS_MCP.Hospital.Management.System.Model.context.Server.service;

import com.HMS_MCP.Hospital.Management.System.Model.context.Server.dto.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final JdbcChatMemoryRepository chatMemoryRepository;

    public ChatService(ChatClient chatClient, JdbcChatMemoryRepository chatMemoryRepository) {
        this.chatClient = chatClient;
        this.chatMemoryRepository = chatMemoryRepository;
    }

    public ChatResponse chat(String message, String conversationId, String role, String userName, Long profileId) {
        String systemPrompt = buildSystemPrompt(role, userName, profileId);
        String response = chatClient.prompt()
                .system(systemPrompt)
                .user(message)
                .advisors(a -> a.param("chat_memory_conversation_id", conversationId))
                .call()
                .content();
        return new ChatResponse(response, conversationId, LocalDateTime.now());
    }

    public void clearHistory(String conversationId) {
        chatMemoryRepository.deleteByConversationId(conversationId);
    }

    private String buildSystemPrompt(String role, String userName, Long profileId) {
        String base = "You are an AI assistant for Pulse HMS (Hospital Management System). " +
                "You have access to real-time hospital data through integrated tools. " +
                "Always be professional, concise, and accurate. " +
                "Never reveal sensitive data of other patients or users outside the requester's scope.\n\n";

        if (role == null) role = "UNKNOWN";

        return switch (role.toUpperCase()) {
            case "PATIENT" -> base +
                    "You are assisting a PATIENT named '" + userName + "' with patient ID " + profileId + ". " +
                    "SCOPE: You may ONLY provide information about THIS patient's own appointments, prescriptions, " +
                    "medical records, and general health queries. " +
                    "You MUST NOT reveal any other patient's data, doctor details beyond name/specialization, " +
                    "or any administrative/financial information. " +
                    "When fetching data, always use patientId = " + profileId + ". " +
                    "You can help the patient: view their appointments, check their prescriptions, " +
                    "see their medical records, and answer general health questions.";

            case "DOCTOR" -> base +
                    "You are assisting a DOCTOR named '" + userName + "' with doctor ID " + profileId + ". " +
                    "SCOPE: You may access and manage appointments assigned to doctorId = " + profileId + ", " +
                    "create/view medical reports and prescriptions for your patients, " +
                    "look up patient profiles for patients under your care, and view pharmacy/medicine data. " +
                    "You MUST NOT modify other doctors' appointments or access unrelated admin data. " +
                    "When fetching doctor-specific data, always use doctorId = " + profileId + ".";

            case "ADMIN" -> base +
                    "You are assisting a HOSPITAL ADMIN named '" + userName + "'. " +
                    "SCOPE: You have FULL access to all hospital data including patients, doctors, appointments, " +
                    "medicines, inventory, and sales. " +
                    "You can add/update/delete any entity, view analytics, and manage the entire system. " +
                    "Use your tools responsibly and confirm before performing destructive actions.";

            default -> base +
                    "Your role is not recognized. Please provide read-only general hospital information. " +
                    "Do not access or modify any sensitive patient or doctor data.";
        };
    }
}
