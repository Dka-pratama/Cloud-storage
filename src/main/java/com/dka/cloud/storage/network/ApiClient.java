/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dka.cloud.storage.network;

import com.dka.cloud.storage.util.ConfigLoader;
import com.dka.cloud.storage.session.Session;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.*;

/**
 *
 * @author andik
 */
public class ApiClient {

    protected static final String BASE_URL = ConfigLoader.get("api.base.url");
    private final HttpClient client;

    public ApiClient() {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    // Hasil request, dibungkus biar gampang dicek sukses/gagal
    public static class ApiResponse {
        public final int statusCode;
        public final String body;

        public ApiResponse(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }

        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }
    }

    // ================= GET =================
    protected ApiResponse get(String endpoint) throws Exception {
        HttpRequest request = baseRequest(endpoint)
                .GET()
                .build();

        return execute(request);
    }

    // ================= POST =================
    protected ApiResponse post(String endpoint, String jsonBody) throws Exception {
        HttpRequest request = baseRequest(endpoint)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .header("Content-Type", "application/json")
                .build();

        return execute(request);
    }

    // ================= Helper: base request + header umum =================
    private HttpRequest.Builder baseRequest(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint));

        // Otomatis tempel token JWT kalau user sudah login
        String token = Session.getInstance().getToken();
        if (token != null && !token.isEmpty()) {
            builder.header("Authorization", "Bearer " + token);
        }

        return builder;
    }

    // ================= Eksekusi request =================
    private ApiResponse execute(HttpRequest request) throws Exception {
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return new ApiResponse(response.statusCode(), response.body());
    }
}