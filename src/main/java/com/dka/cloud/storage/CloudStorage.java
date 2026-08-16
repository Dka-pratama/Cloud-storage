/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.dka.cloud.storage;

import com.dka.cloud.storage.session.Session;
import com.dka.cloud.storage.ui.*;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;

/**
 *
 * @author andik
 */
public class CloudStorage {
    
    public static void main(String[] args) {
        java.awt.Font globalFont = new java.awt.Font("Cascadia Code", java.awt.Font.PLAIN, 14);
        
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, globalFont);
            }
        }
        try{
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            System.err.println("gagal initialize flatlaf");
        }
        
        SwingUtilities.invokeLater(() -> {
            boolean hasValidToken = Session.getInstance().tryRestoreSession();

            if (hasValidToken) {
                new DashboardForm().setVisible(true);
            } else {
                new LoginForm().setVisible(true);
            }
        });
    }
}
