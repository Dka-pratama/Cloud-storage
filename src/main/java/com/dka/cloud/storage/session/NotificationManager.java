/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dka.cloud.storage.session;

import com.dka.cloud.storage.model.AppNotification;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author andik
 */
public class NotificationManager {

    private static NotificationManager instance;
    private final List<AppNotification> notifications = new ArrayList<>();
    private Listener listener;

    public interface Listener {
        void onNotificationsChanged();
    }

    private NotificationManager() {}

    public static NotificationManager getInstance() {
        if (instance == null) instance = new NotificationManager();
        return instance;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void addNotification(String message) {
        notifications.add(0, new AppNotification(message)); // terbaru di atas
        notifyChanged();
    }

    public List<AppNotification> getAll() {
        return notifications;
    }

    public int getUnreadCount() {
        return (int) notifications.stream().filter(n -> !n.isRead()).count();
    }

    public void markAllAsRead() {
        notifications.forEach(AppNotification::markAsRead);
        notifyChanged();
    }

    public void clearAll() {
        notifications.clear();
        notifyChanged();
    }

    private void notifyChanged() {
        if (listener != null) {
            SwingUtilities.invokeLater(() -> listener.onNotificationsChanged());
        }
    }
}
