package com.engine.dlqtriage.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class LlmInferenceService {

    private final ChatClient chatClient;

    public LlmInferenceService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String diagnose(String payload, String stackTrace) {
        String systemPrompt = """
            You are a senior backend diagnostic engineer specializing in banking event streams.
            Analyze the provided malformed JSON payload and the Java stack trace.
            
            Provide a concise, pipe-separated response strictly in this format:
            ROOT CAUSE: [Your 1-2 sentence analysis] | SUGGESTION: [How to fix the payload or code]
            
            Do not include any Markdown, pleasantries, or additional formatting.
            """;

        String userPrompt = String.format("PAYLOAD:\n%s\n\nSTACK TRACE:\n%s", payload, stackTrace);

        try {
            return chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();
        } catch (Exception e) {
            return "ROOT CAUSE: LLM Inference Failed (" + e.getMessage() + ") | SUGGESTION: Manual triage required.";
        }
    }
}