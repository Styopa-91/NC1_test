package com.project.nc1_test.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.nc1_test.models.NewsArticle;
import com.project.nc1_test.models.NewsArticleDto;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class NewsArticlesService {
    private static final String BASE_URL = "http://localhost:8081/api/news";

    private final HttpClient httpClient;

    private final ObjectMapper objectMapper;

    public NewsArticlesService() {
        this.httpClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public List<NewsArticle> getAllArticles() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // Deserialize JSON response to List<NewsArticle>
            // e.g., using Jackson ObjectMapper or Gson
            return deserializeToList(response.body());
        } catch (Exception e) {
            e.printStackTrace();
            // Handle the exception
        }
        return List.of();
    }

    public List<NewsArticle> getFilteredArticles(String period) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/byPeriod/" + period))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // Deserialize JSON response to List<NewsArticle>
            // e.g., using Jackson ObjectMapper or Gson
            return deserializeToList(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    public NewsArticle createArticle(NewsArticleDto article) throws JsonProcessingException {
        // Convert article to JSON
        String json = serializeArticle(article);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // Deserialize JSON response to NewsArticle
            return deserializeToNewsArticle(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public NewsArticle updateArticle(Long id, NewsArticleDto updatedArticle) throws JsonProcessingException {
        // Convert updatedArticle to JSON
        String json = serializeArticle(updatedArticle);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // Deserialize JSON response to NewsArticle
            return deserializeToNewsArticle(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteArticle(Long id) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();

        try {
            httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper methods for (de)serialization (implementation depends on the choice of JSON library)
    public List<NewsArticle> deserializeToList(String jsonResponse) throws JsonProcessingException {
        return objectMapper.readValue(jsonResponse, objectMapper.getTypeFactory().constructCollectionType(List.class, NewsArticle.class));
    }
    private NewsArticle deserializeToNewsArticle(String json) {
        // Implement JSON to NewsArticle deserialization
        return null;
    }

    private String serializeArticle(NewsArticleDto article) throws JsonProcessingException {
        return objectMapper.writeValueAsString(article);
    }
}

