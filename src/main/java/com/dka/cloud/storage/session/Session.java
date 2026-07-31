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
    
    private Session(){}
    
    public static Session getInstance(){
        if (instance == null) instance = new Session();
        return instance;
    }
    
    public void setToken(String token) {this.token = token;}
    public String getToken() {return token; }
    public void cleanSession() {this.token = null;}
}
