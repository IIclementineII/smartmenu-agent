package com.smartmenu.backend.controller;

import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private static final String PROJECT_ID = "smartmenu-agent";
    private static final String AGENT_ID = "b44be8e2-9d9e-4e68-8405-400a8d2fd38a";
    private static final String LOCATION = "us-central1";

    private GoogleCredentials getCredentials() throws IOException {
        String credentialsJson = System.getenv("GOOGLE_CREDENTIALS_JSON");
        if (credentialsJson != null) {
            return GoogleCredentials
                    .fromStream(new java.io.ByteArrayInputStream(credentialsJson.getBytes()))
                    .createScoped("https://www.googleapis.com/auth/cloud-platform");
        }
        var resource = new ClassPathResource("smartmenu-credentials.json");
        return GoogleCredentials
                .fromStream(resource.getInputStream())
                .createScoped("https://www.googleapis.com/auth/cloud-platform");
    }

    @PostMapping
    public ResponseEntity<?> chat(@RequestBody Map<String, String> body) {
        String message = body.getOrDefault("message", "");
        String sessionId = body.getOrDefault("sessionId", "session-" + UUID.randomUUID());

        try {
            GoogleCredentials credentials = getCredentials();
            credentials.refreshIfExpired();
            String accessToken = credentials.getAccessToken().getTokenValue();

            String url = String.format(
                    "https://%s-dialogflow.googleapis.com/v3/projects/%s/locations/%s/agents/%s/sessions/%s:detectIntent",
                    LOCATION, PROJECT_ID, LOCATION, AGENT_ID, sessionId
            );

            Map<String, Object> requestBody = Map.of(
                    "queryInput", Map.of(
                            "text", Map.of("text", message),
                            "languageCode", "en"
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            Map responseBody = response.getBody();
            Map queryResult = (Map) responseBody.get("queryResult");
            List responseMessages = (List) queryResult.get("responseMessages");

            StringBuilder replyText = new StringBuilder();
            for (Object msg : responseMessages) {
                Map msgMap = (Map) msg;
                if (msgMap.containsKey("text")) {
                    Map textMap = (Map) msgMap.get("text");
                    List texts = (List) textMap.get("text");
                    if (texts != null) {
                        for (Object t : texts) replyText.append(t);
                    }
                }
            }

            return ResponseEntity.ok(Map.of("reply", replyText.toString().trim()));

        } catch (Exception e) {
            log.error("Chat error: {}", e.getMessage());
            return ResponseEntity.ok(Map.of("reply", "Sorry, I couldn't process your request. Please try again."));
        }
    }
}