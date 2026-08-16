/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dka.cloud.storage.panels;

import com.dka.cloud.storage.model.AppNotification;
import com.dka.cloud.storage.session.NotificationManager;
import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author andik
 */
public class NotificationPopup extends JPopupMenu {

    public NotificationPopup() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(320, 400));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Notifikasi");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 14f));
        title.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JButton btnClear = new JButton("Bersihkan");
        btnClear.setBorderPainted(false);
        btnClear.setContentAreaFilled(false);
        btnClear.addActionListener(e -> {
            NotificationManager.getInstance().clearAll();
            setVisible(false);
        });

        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(btnClear, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        var notifications = NotificationManager.getInstance().getAll();

        if (notifications.isEmpty()) {
            JLabel empty = new JLabel("Tidak ada notifikasi", SwingConstants.CENTER);
            empty.setForeground(Color.GRAY);
            empty.setBorder(BorderFactory.createEmptyBorder(40, 0, 40, 0));
            listPanel.add(empty);
        } else {
            for (AppNotification notif : notifications) {
                listPanel.add(createNotificationRow(notif));
            }
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        NotificationManager.getInstance().markAllAsRead();
    }

    private JPanel createNotificationRow(AppNotification notif) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        JLabel lblMessage = new JLabel("<html>" + notif.getMessage() + "</html>");
        JLabel lblTime = new JLabel(notif.getTimestamp().format(DateTimeFormatter.ofPattern("dd MMM, HH:mm")));
        lblTime.setFont(lblTime.getFont().deriveFont(10f));
        lblTime.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(lblMessage);
        textPanel.add(lblTime);

        row.add(textPanel, BorderLayout.CENTER);
        return row;
    }
}
