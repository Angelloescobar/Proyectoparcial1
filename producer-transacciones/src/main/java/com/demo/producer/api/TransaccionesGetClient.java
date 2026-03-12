package com.demo.producer.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.demo.producer.model.LoteTransacciones;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TransaccionesGetClient {

    private final String url;
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public TransaccionesGetClient(String url) {
        this.url = url;
    }

    public LoteTransacciones obtenerTransacciones() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error al consumir API: " + response.statusCode());
        }

        return mapper.readValue(response.body(), LoteTransacciones.class);
    }
}