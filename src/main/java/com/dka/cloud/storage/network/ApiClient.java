/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dikka.cloud.storage.network;

import com.dikka.cloud.storage.util.ConfigLoader;
import com.dikka.cloud.storage.session.Session;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 *
 * @author andik
 */
public class ApiClient {
    protected static final String BASE_URL = ConfigLoader.get("api.base.url");;
    private final HttpClient client = HttpClient.newHttpClient();
    
    protected HttpRequest.Builder authorizedRequest(String endpoint){
        String token = com.dikka.cloud.storage.session.Session.getInstance().getToken();
        return HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Authorization","Bearer" + token);
    }
    protected HttpResponse<String> send(HttpRequest request) throws Exception {
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
    }

