package com.sudhii.service;

import com.sudhii.model.DeepSeekDBModel;
import com.sudhii.repository.DeepSeekRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ModelServiceTest {

    @Mock
    private DeepSeekRepository deepSeekRepository;

    @InjectMocks
    private ModelService modelService;

    private DeepSeekDBModel mockChat;

    @BeforeEach
    void setUp() {
        mockChat = new DeepSeekDBModel();
        mockChat.setId("test-id-123");
        mockChat.setTitle("Test Title");
        mockChat.setConverstionList(new ArrayList<>());
    }

    @Test
    void testCreateChat() {
        when(deepSeekRepository.insert(any(DeepSeekDBModel.class))).thenReturn(mockChat);
        
        DeepSeekDBModel created = modelService.createChat();
        
        assertNotNull(created);
        assertEquals("test-id-123", created.getId());
        verify(deepSeekRepository, times(1)).insert(any(DeepSeekDBModel.class));
    }

    @Test
    void testAddNewRecord_TruncatesQueueAt10() {
        // Pre-fill with 10 records
        List<DeepSeekDBModel.Conversation> conversations = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            DeepSeekDBModel.Conversation conv = new DeepSeekDBModel.Conversation();
            conv.setQuestion("Q" + i);
            conv.setAnswer("A" + i);
            conversations.add(conv);
        }
        mockChat.setConverstionList(conversations);

        when(deepSeekRepository.findById("test-id-123")).thenReturn(Optional.of(mockChat));
        when(deepSeekRepository.save(any(DeepSeekDBModel.class))).thenReturn(mockChat);

        // Add 11th record
        DeepSeekDBModel result = modelService.addNewRecord("test-id-123", "New Request", "New Response");

        assertNotNull(result);
        assertEquals(10, result.getConverstionList().size());
        assertEquals("Q1", result.getConverstionList().get(0).getQuestion()); // Oldest removed
        assertEquals("New Request", result.getConverstionList().get(9).getQuestion()); // Newest added
    }

    @Test
    void testAddNewRecord_GeneratesTitleIfNull() {
        mockChat.setTitle(null);
        when(deepSeekRepository.findById("test-id-123")).thenReturn(Optional.of(mockChat));
        when(deepSeekRepository.save(any(DeepSeekDBModel.class))).thenReturn(mockChat);

        String longRequest = "This is a very long request that should exceed thirty characters and be truncated";
        
        modelService.addNewRecord("test-id-123", longRequest, "Response");
        
        assertNotNull(mockChat.getTitle());
        assertTrue(mockChat.getTitle().endsWith("..."));
        assertEquals(33, mockChat.getTitle().length()); // 30 chars + "..."
    }
}
