package com.hhn.studyChat.service;

import com.hhn.studyChat.model.RAGDocument;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RAGServiceTest {

    private RAGService ragService;
    private EmbeddingStore<TextSegment> embeddingStore;
    private EmbeddingModel embeddingModel;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        CrawlerService crawlerService = mock(CrawlerService.class);
        ragService = new RAGService(crawlerService);

        embeddingStore = mock(EmbeddingStore.class);
        embeddingModel = mock(EmbeddingModel.class);

        ReflectionTestUtils.setField(ragService, "embeddingModel", embeddingModel);

        Map<String, EmbeddingStore<TextSegment>> stores = new HashMap<>();
        stores.put("job-1", embeddingStore);
        ReflectionTestUtils.setField(ragService, "embeddingStores", stores);
    }

    @Test
    void testFindRelevantDocumentsReturnsSegmentText() {
        String segmentText = "This is exact chunk content from embedding match";
        Map<String, String> meta = new HashMap<>();
        meta.put("url", "https://example.com/page");
        meta.put("title", "Page Title");
        meta.put("category", "test");

        TextSegment segment = TextSegment.from(segmentText, Metadata.from(meta));
        EmbeddingMatch<TextSegment> match = new EmbeddingMatch<>(0.9, "1", new Embedding(new float[]{0.1f}), segment);

        dev.langchain4j.data.embedding.Embedding queryEmb = new dev.langchain4j.data.embedding.Embedding(new float[]{0.1f});

        when(embeddingModel.embed(anyString())).thenReturn(dev.langchain4j.model.output.Response.from(queryEmb));
        when(embeddingStore.findRelevant(any(Embedding.class), anyInt()))
                .thenReturn(Collections.singletonList(match));

        List<RAGDocument> results = ragService.findRelevantDocuments("job-1", "query", 5);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(segmentText, results.get(0).getContent());
        assertEquals("https://example.com/page", results.get(0).getUrl());
        assertEquals("Page Title", results.get(0).getTitle());
        assertEquals("test", results.get(0).getCategory());
    }
}
