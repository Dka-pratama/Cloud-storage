/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dka.cloud.storage.session;

import java.util.prefs.Preferences;
/**
 *
 * @author andik
 */
public class TokenStorage {
    private static final Preferences prefs = Preferences.userNodeForPackage(TokenStorage.class);
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_SAVED_AT = "token_saved_at";
    private static final long EXPIRY_MILLIS = 7L * 24 * 60 * 60 * 1000; // 7 hari

    public static void saveToken(String token) {
        prefs.put(KEY_TOKEN, token);
        prefs.putLong(KEY_SAVED_AT, System.currentTimeMillis());
    }

    public static String getValidToken() {
        String token = prefs.get(KEY_TOKEN, null);
        long savedAt = prefs.getLong(KEY_SAVED_AT, 0);

        if (token == null || savedAt == 0) {
            return null; // gak ada token tersimpan
        }

        long elapsed = System.currentTimeMillis() - savedAt;
        if (elapsed > EXPIRY_MILLIS) {
            clearToken(); // udah lewat 7 hari, hapus
            return null;
        }

        return token; // masih valid
    }

    public static void clearToken() {
        prefs.remove(KEY_TOKEN);
        prefs.remove(KEY_SAVED_AT);
    }
}
