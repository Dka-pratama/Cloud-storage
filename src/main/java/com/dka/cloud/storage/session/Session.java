/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dka.cloud.storage.session;

/**
 *
 * @author andik
 */
public class Session {
    private static Session instance;
    private String token;

    private Session() {}

    public static Session getInstance() {
        if (instance == null) instance = new Session();
        return instance;
    }

    public void setToken(String token) {
        this.token = token;
        TokenStorage.saveToken(token); // otomatis simpan ke disk juga
    }

    public String getToken() {
        return token;
    }

    public void clear() {
        this.token = null;
        TokenStorage.clearToken();
    }

    // Dipanggil sekali pas app start — cek token tersimpan
    public boolean tryRestoreSession() {
        String saved = TokenStorage.getValidToken();
        if (saved != null) {
            this.token = saved;
            return true;
        }
        return false;
    }
}
