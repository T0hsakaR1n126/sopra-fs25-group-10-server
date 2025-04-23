package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.Country;
import ch.uzh.ifi.hase.soprafs24.repository.GameRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cglib.core.internal.LoadingCache;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

@Service
@Transactional
public class UtilService {
    private static final int FILL_SIZE = 10; //10 countires in the beginning instead of 20
    private static final int HINT_NUMBER = 6; //changed to 6 from 5 since first hint is free

    private static final String GEMINI_API_KEY = "AIzaSyDOukvhZmaQlP38T1bdTGGnc5X-TYRr_Gc";
    private static final String MODEL_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + GEMINI_API_KEY;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Queue<Map<Country, List<Map<String, Object>>>> hintCache = new ConcurrentLinkedDeque<>();

    public Queue<Map<Country, List<Map<String, Object>>>> getHintCache() {
        return hintCache;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void preloadHints() {
        System.out.println("Preloading hints...");
        refillCache();
        System.out.println("Preloading hints done! Cache size: " + hintCache.size());
    }

    public void refillCache() {
        int missing = FILL_SIZE - hintCache.size();
        int attempts = 0;

        while (hintCache.size() < FILL_SIZE && attempts < FILL_SIZE * 2) {
            try {
                Map<Country, List<Map<String, Object>>> newHint = generateClues(HINT_NUMBER);
                if (newHint != null && !newHint.isEmpty()) {
                    hintCache.add(newHint);
                }
                Thread.sleep(1500);
            } catch (Exception e) {
                System.err.println("Failed to generate hint (attempt " + (attempts + 1) + "): " + e.getMessage());
            }
            attempts++;
        }

        System.out.println("Cache preloaded with " + hintCache.size() + " hints");
    }

    @Async
    public void refillAsync() {
        System.out.println("Async refill running on thread: " + Thread.currentThread().getName());

        Map<Country, List<Map<String, Object>>> hint = generateClues(HINT_NUMBER);
        if (hint != null && !hint.isEmpty()) {
            hintCache.add(hint);
        }
    }

    //<country, clue, difficulty(int)>
    public Map<Country, List<Map<String, Object>>> generateClues(int clueNumber) {
        try {
            Country[] countries = Country.values();
            Country targetCountry = countries[new Random().nextInt(countries.length)];
            String prompt = buildPrompt(targetCountry, clueNumber);
            String payload = buildPayloadJson(prompt);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(MODEL_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return extractClues(response.body(), targetCountry);
            } else {
                throw new RuntimeException("LLM API failed: " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating clues", e);
        }
    }

    private String buildPrompt(Country country, int clueNumber) {
        return """
                You are an AI that generates **single-sentence** geography quiz clues for a game.

                ### **Task**
                Given the country: %s, generate **%d different geography clues** that **do not directly mention the country's name and any information provided below**. IMPORTANT: AVOID GENERATING ANYTHING RELATED TO POLITICS AND HISTORY. 
                Each clue should have a difficulty score from **1 to %d**, increasing progressively.

                ### **Strict Output Format**
                Return **exactly** %d clues in the following format, NEVER include any other words or characters!!:
                1. Clue: [Provide a single-sentence clue] - Difficulty: 1
                ...
                %d. Clue: [Provide a single-sentence clue] - Difficulty: %d
                Do not miss any line.
                """.formatted(
                country, clueNumber, clueNumber, clueNumber, clueNumber, clueNumber
        );
    }

    private String buildPayloadJson(String promptText) throws Exception {
        Map<String, Object> textPart = Map.of("text", promptText);
        Map<String, Object> content = Map.of("parts", List.of(textPart));
        Map<String, Object> payload = Map.of("contents", List.of(content));

        return objectMapper.writeValueAsString(payload);
    }

    private Map<Country, List<Map<String, Object>>> extractClues(String responseBody, Country country) throws Exception {
        JsonNode root = objectMapper.readTree(responseBody);

        JsonNode parts = root
                .path("candidates").get(0)
                .path("content")
                .path("parts");

        if (!parts.isArray() || parts.isEmpty()) {
            throw new RuntimeException("Invalid LLM response format");
        }

        String outputText = parts.get(0).path("text").asText();
        String[] lines = outputText.split("\n");

        List<Map<String, Object>> clueList = new ArrayList<>();

        for (String line : lines) {
            line = line.trim();
            if (line.matches("^\\d+\\. Clue: .* - Difficulty: \\d+$")) {
                String clueText = line.replaceAll("^\\d+\\. Clue: (.*) - Difficulty: \\d+$", "$1").trim();
                int difficulty = Integer.parseInt(
                        line.replaceAll("^\\d+\\. Clue: .* - Difficulty: (\\d+)$", "$1").trim()
                );

                Map<String, Object> clueMap = new HashMap<>();
                clueMap.put("text", clueText);
                clueMap.put("difficulty", difficulty);

                clueList.add(clueMap);
            }
        }

        Map<Country, List<Map<String, Object>>> result = new HashMap<>();
        result.put(country, clueList);
        return result;
    }

}