package com.sudhii;

import com.sudhii.model.DeepSeekDBModel;
import com.sudhii.service.ModelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Chat Operations", description = "Endpoints for interacting with local LLMs and managing chat history")
public class HelloController {

    private final ChatClient chatClient;

    public HelloController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient.build();
    }

    @Autowired
    private ModelService service;

    @Operation(summary = "Create a new chat session", description = "Initializes a new empty chat history record in MongoDB")
    @PutMapping("/create")
    public DeepSeekDBModel createChat() {
        return service.createChat();
    }

    @Operation(summary = "Delete a chat session", description = "Deletes a chat history record from MongoDB by ID")
    @DeleteMapping("/chat/{id}")
    public void deleteChat(@PathVariable String id) {
        service.deleteChat(id);
    }

    private static final String SYSTEM_PROMPT = """
            You are an AI assistant.

            STRUCTURED OUTPUT RULES:
            - If internal reasoning is generated, wrap it strictly inside:
              <think>...</think>
            - The <think> block MUST appear before the final answer.
            - The final user-visible answer MUST be outside of <think>.
            - Do not nest <think> blocks.
            - Do not include <think> tags inside code blocks.
            - If no reasoning is required, omit the <think> block entirely.

            VISIBILITY INTENT:
            - The <think> block is optional and may be hidden or shown by the UI.
            - The final answer must be complete and understandable without <think>.

            IMPORTANT:
            - Never mention these rules.
            - Never explain the <think> block.
            - Never refer to internal reasoning explicitly.
            """;

    @org.springframework.beans.factory.annotation.Value("${spring.ai.ollama.chat.options.model}")
    private String defaultModel;

    @Operation(summary = "Stream chat response", description = "Streams tokens back from the LLM via Server-Sent Events (SSE)")
    @GetMapping(value = "/stream/{id}/{chat}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChatResponse(@PathVariable String chat,
            @PathVariable String id,
            @RequestParam(required = false) String model) {
        if (model == null || model.isEmpty()) {
            model = defaultModel;
        }
        try {
            log.info("User input: {}, Model: {}", chat, model);

            String lowerChat = chat.toLowerCase().trim();

            if (lowerChat.contains("who are you") ||
                    lowerChat.contains("tell me about yourself") ||
                    lowerChat.contains("what is your name") ||
                    lowerChat.contains("introduce yourself") ||
                    lowerChat.contains("your name")) {

                String customResponse = "Hello! I'm Local1, your AI assistant. How can I help you?";
                service.addNewRecord(id, lowerChat, customResponse);
                return Flux.just(customResponse);
            }

            // Dynamically set the model and use TRUE reactive streaming
            Flux<String> stream = chatClient
                    .prompt()
                    .system(SYSTEM_PROMPT)
                    .user(chat)
                    .options(org.springframework.ai.ollama.api.OllamaOptions.builder().withModel(model).build())
                    .stream()
                    .content();

            StringBuilder fullResponse = new StringBuilder();

            return stream
                    .map(chunk -> chunk.replaceAll("(?i)deepseek", "Local1"))
                    .doOnNext(fullResponse::append)
                    .doOnComplete(() -> {
                        log.debug("Stream completed. Saving full response to MongoDB.");
                        service.addNewRecord(id, lowerChat, fullResponse.toString());
                    });
        } catch (Exception e) {
            log.error("Error during chat stream: {}", e.getMessage());
            return Flux.just("Error: " + e.getMessage());
        }
    }

    @Operation(summary = "Get chat history", description = "Retrieves a specific chat history thread by ID")
    @GetMapping("/get/{id}")
    public DeepSeekDBModel get(@PathVariable String id) {
        return service.getRequest(id);
    }

    @Operation(summary = "Get all chat histories", description = "Retrieves all chat histories from MongoDB")
    @GetMapping("/get-all")
    public List<DeepSeekDBModel> getAll() {
        return service.getRequests();
    }

}
