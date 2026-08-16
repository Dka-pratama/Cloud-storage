/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dka.cloud.storage.model;

/**
 *
 * @author andik
 */

import java.time.LocalDateTime;

public class AppNotification {
    private final String message;
    private final LocalDateTime timestamp;
    private boolean read;
    
    public AppNotification(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }
    
    public String getMessage() {return message;}
    public LocalDateTime getTimestamp() {return timestamp;}
    public boolean isRead() {return read;}
    public void markAsRead() {this.read = true;}
}
