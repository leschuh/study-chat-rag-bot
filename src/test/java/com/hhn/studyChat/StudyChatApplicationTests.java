package com.hhn.studyChat;

import com.hhn.studyChat.service.RAGService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class StudyChatApplicationTests {

	@Autowired
	private RAGService ragService;

	@Test
	void contextLoads() {
		assertNotNull(ragService);
	}

	@Test
	void testOfflineGenerateResponse() {
		String response = ragService.generateResponse("Test Query", "Test Context");
		assertNotNull(response);
		assertTrue(response.contains("[Offline-Modus]") || response.contains("Antwort"));
	}

}

