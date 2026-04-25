package com.vanapp.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GeocodingService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public double[] geocodificarEndereco(String endereco) {
        try {
            String url = "https://nominatim.openstreetmap.org/search?q=" 
                + endereco.replace(" ", "+") 
                + "&format=json&limit=1";

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "vanapp-faculdade/1.0");

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class
            );

            JsonNode node = objectMapper.readTree(response.getBody());

            if (node.isArray() && node.size() > 0) {
                double lat = node.get(0).get("lat").asDouble();
                double lon = node.get(0).get("lon").asDouble();
                return new double[]{lat, lon};
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao geocodificar endereço: " + e.getMessage());
        }

        throw new RuntimeException("Endereço não encontrado: " + endereco);
    }
}