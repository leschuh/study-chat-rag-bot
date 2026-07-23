package com.hhn.studyChat.service;

import com.hhn.studyChat.model.ChatMessage;
import com.hhn.studyChat.model.RAGDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChatServiceTest {

    private RAGService ragService;
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        ragService = Mockito.mock(RAGService.class);
        chatService = new ChatService(ragService);
    }

    @Test
    void testPrepareContextIncludesFullSegmentContentWithoutTruncation() {
        String longContent = "A".repeat(600); // 600 characters
        RAGDocument doc = RAGDocument.create("job1", "https://example.com", "Test Title", longContent, "test", "/path");

        when(ragService.findRelevantDocuments(anyString(), anyString(), anyInt()))
                .thenReturn(Collections.singletonList(doc));
        when(ragService.generateResponse(anyString(), anyString()))
                .thenReturn("Test response");

        ChatMessage message = ChatMessage.builder()
                .jobId("job1")
                .userMessage("Test Question")
                .build();
        chatService.processMessage(message);

        ArgumentCaptor<String> contextCaptor = ArgumentCaptor.forClass(String.class);
        verify(ragService).generateResponse(anyString(), contextCaptor.capture());

        String capturedContext = contextCaptor.getValue();
        assertTrue(capturedContext.contains(longContent), "Context should contain the full content without 500-char truncation");
        assertFalse(capturedContext.contains("..."), "Context should not append truncation ellipsis");
    }
}
