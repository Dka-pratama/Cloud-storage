/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dka.cloud.storage.session;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author andik
 */
public class IdleNotificationTracker {
    private static final Set<String> notifiedFiles = new HashSet<>();

    public static boolean alreadyNotified(String objectName) {
        return notifiedFiles.contains(objectName);
    }

    public static void markNotified(String objectName) {
        notifiedFiles.add(objectName);
    }
}
