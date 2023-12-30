package com.speechtotext.core.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.speechtotext.core.dto.request.OpenAIMessageRequest;
import com.speechtotext.core.dto.request.OpenAISummarizeRequest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class OpenAISpeechSummarizer implements TranscriptSummarizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenAISpeechSummarizer.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Value("${openai.api.completion.url}")
    private String apiUrl;

    private WebClient webClient;

    @PostConstruct
    public void init() {
        webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAiApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .build();
    }

    @Override
    public String summarize(String transcript) {
        String requestBody = createRequestBody(transcript);
        LOGGER.info("Sending completion request with body: {}", requestBody);
        String summary =  webClient
                .post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(r -> new JSONObject(r).getJSONArray("choices")
                        .getJSONObject(0).getJSONObject("message").getString("content").trim())
                .block();
        LOGGER.info("Received summary: {}", summary);
        return summary;
    }

    private static String createRequestBody(String transcript) {
        try {
            Path prompt = Paths.get("src/main/resources/prompt.json");
            OpenAISummarizeRequest request = MAPPER.readValue(prompt.toFile(), OpenAISummarizeRequest.class);
            OpenAIMessageRequest msg = request.getMessages().get(3);
            msg.setContent(msg.getContent() + transcript);
            return MAPPER.writeValueAsString(request);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
