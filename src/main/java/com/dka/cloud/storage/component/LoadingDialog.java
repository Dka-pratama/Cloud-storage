/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dka.cloud.storage.component;

import javax.swing.*;
import java.awt.*;
/**
 *
 * @author andik
 */
public class LoadingDialog extends JDialog{
    public LoadingDialog(Frame parent) {
        super(parent, "Memproses...", true);
        
        JPanel panel = new JPanel(new BorderLayout(10,10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel label = new JLabel("Mohon Tungu, sedang menghubungkan ke server....");
        JProgressBar progressbar = new JProgressBar();
        progressbar.setIndeterminate(true);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(progressbar, BorderLayout.CENTER);
        
        add(panel);
        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    }
}
