/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.dka.cloud.storage;

import com.dka.cloud.storage.ui.*;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;

/**
 *
 * @author andik
 */
public class CloudStorage {

    public static void main(String[] args) {
        try{
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("gagal initialize flatlaf");
        }
        
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}
