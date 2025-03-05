package com.sudhii;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Model Discovery", description = "Endpoints for discovering available local LLMs from Ollama")
public class ModelController {

    @Value("${spring.ai.ollama.chat.options.model}")
    private String defaultModel;

    @Value("${ollama.api.url:http://localhost:11434}")
    private String ollamaApiUrl;

    @Operation(summary = "Get available models", description = "Fetches a list of available models directly from the local Ollama instance")
    @GetMapping("/models")
    public List<String> getAvailableModels() {
        List<String> availableModels = new ArrayList<>();
        // Always add the default configured model first
        if (defaultModel != null && !defaultModel.isEmpty()) {
            availableModels.add(defaultModel);
        }

        try {
            RestTemplate restTemplate = new RestTemplate();
            // Ollama API endpoint for tags
            String url = ollamaApiUrl + "/api/tags";
            Map result = restTemplate.getForObject(url, Map.class);

            if (result != null && result.containsKey("models")) {
                List<Map<String, Object>> models = (List<Map<String, Object>>) result.get("models");
                List<String> ollamaModels = models.stream()
                        .map(m -> (String) m.get("name"))
                        .filter(name -> !name.equals(defaultModel)) // Avoid duplicates
                        .collect(Collectors.toList());
                availableModels.addAll(ollamaModels);
                log.info("Successfully fetched {} models from Ollama.", ollamaModels.size());
            }
        } catch (Exception e) {
            log.error("Failed to fetch models from Ollama at {}. Using default and fallbacks. Error: {}", ollamaApiUrl, e.getMessage());
            if (availableModels.isEmpty()) {
                log.warn("No models found nor default configured. Adding hardcoded fallbacks.");
                availableModels.add("qwen3:1.7b");
                availableModels.add("deepcoder:1.5b");
            } else {
                // Ensure deepcoder:1.5b is present if the user wants to switch back
                if (!availableModels.contains("deepcoder:1.5b")) {
                    availableModels.add("deepcoder:1.5b");
                }
                if (!availableModels.contains("qwen3:1.7b")) {
                    availableModels.add("qwen3:1.7b");
                }
            }
        }
        return availableModels;
    }
}
