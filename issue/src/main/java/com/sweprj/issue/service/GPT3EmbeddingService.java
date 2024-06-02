package com.sweprj.issue.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GPT3EmbeddingService {

    @Value("${gpt3.api.key}")
    private String apiKey;

    private static final String EMBEDDING_URL = "https://api.openai.com/v1/embeddings";

    public double[] getEmbedding(String text) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Content-Type", "application/json");

        JSONObject request = new JSONObject();
        request.put("input", text);
        request.put("model", "text-embedding-ada-002");

        HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);
        ResponseEntity<String> response = restTemplate.exchange(EMBEDDING_URL, HttpMethod.POST, entity, String.class);

        JSONObject responseObject = new JSONObject(response.getBody());
        JSONArray embeddingsArray = responseObject.getJSONArray("data").getJSONObject(0).getJSONArray("embedding");

        double[] embeddings = new double[embeddingsArray.length()];
        for (int i = 0; i < embeddingsArray.length(); i++) {
            embeddings[i] = embeddingsArray.getDouble(i);
        }

        return embeddings;
    }
}
