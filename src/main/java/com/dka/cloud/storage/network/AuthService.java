/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dka.cloud.storage.network;

import com.google.gson.*;
import com.dka.cloud.storage.session.Session;
import javax.swing.SwingUtilities;
/**
 *
 * @author andik
 */
public class AuthService extends ApiClient {

    public interface AuthCallback {
        void onSuccess();
        void onError(String message);
    }

    public void login(String username, String password, AuthCallback callback) {
        new Thread(() -> {
            try {
                JsonObject body = new JsonObject();
                body.addProperty("email", username);
                body.addProperty("password", password);

                ApiResponse response = post("/auth/login", body.toString());

                if (response.isSuccess()) {
                    JsonObject json = JsonParser.parseString(response.body).getAsJsonObject();
                    String token = json.get("token").getAsString();
                    Session.getInstance().setToken(token);

                    SwingUtilities.invokeLater(callback::onSuccess);
                } else {
                    SwingUtilities.invokeLater(() ->
                        callback.onError("Login gagal: HTTP " + response.statusCode));
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> callback.onError(e.getMessage()));
            }
        }).start();
    }
}