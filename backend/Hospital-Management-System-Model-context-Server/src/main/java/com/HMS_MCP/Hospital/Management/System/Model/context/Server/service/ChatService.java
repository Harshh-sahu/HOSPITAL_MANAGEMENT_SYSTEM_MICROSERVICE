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

    public ChatResponse chat(String message, String conversationId) {
        String response = chatClient.prompt()
                .user(message)
                .advisors(a -> a.param("chat_memory_conversation_id", conversationId))
                .call()
                .content();
        return new ChatResponse(response, conversationId, LocalDateTime.now());
    }

    public void clearHistory(String conversationId) {
        chatMemoryRepository.deleteByConversationId(conversationId);
    }
}
