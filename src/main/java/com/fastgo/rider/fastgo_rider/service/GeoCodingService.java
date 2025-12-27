package com.fastgo.rider.fastgo_rider.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class GeoCodingService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    
    public Coordinates getCoordinatesStructured(String street, String city, String postalCode) {
        try {
            if (city == null || city.trim().isEmpty()) return null;

            StringBuilder urlBuilder = new StringBuilder("https://nominatim.openstreetmap.org/search?format=json&limit=1");

            if (street != null && !street.isEmpty()) {
                urlBuilder.append("&street=").append(URLEncoder.encode(street, StandardCharsets.UTF_8));
            }
            
            urlBuilder.append("&city=").append(URLEncoder.encode(city, StandardCharsets.UTF_8));

            if (postalCode != null && !postalCode.isEmpty()) {
                urlBuilder.append("&postalcode=").append(URLEncoder.encode(postalCode, StandardCharsets.UTF_8));
            }

            // countrycodes per limitare all'Italia (
            urlBuilder.append("&countrycodes=it");

            String url = urlBuilder.toString();
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "FastGo-Rider-Service/1.0"); 
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // Log l'URL per debug 
            System.out.println("Geocoding Request: " + url);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getBody() != null) {
                JsonNode root = objectMapper.readTree(response.getBody());
                if (root.isArray() && root.size() > 0) {
                    JsonNode firstResult = root.get(0);
                    String lat = firstResult.get("lat").asText();
                    String lon = firstResult.get("lon").asText();
                    return new Coordinates(lat, lon);
                }
            }
        } catch (Exception e) {
            System.err.println("Errore Geocoding Strutturato: " + e.getMessage());
        }
        return null;
    }

    public static class Coordinates {
        public final String lat;
        public final String lon;

        public Coordinates(String lat, String lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }
}