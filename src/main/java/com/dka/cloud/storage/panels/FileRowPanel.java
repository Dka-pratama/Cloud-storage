/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.dka.cloud.storage.panels;

import com.dka.cloud.storage.model.FileItem;
import com.dka.cloud.storage.model.StorageTier;
import com.dka.cloud.storage.service.FileService;
import com.dka.cloud.storage.util.FileIconUtil;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Cursor;
import java.awt.Dimension;
import javax.swing.BorderFactory;


/**
 *
 * @author andik
 */
public class FileRowPanel extends javax.swing.JPanel {

    /**
     * Creates new form FileRowPanel
     */
    public interface FolderClickListener {
        void onFolderClick(String folderPrefix);
    }
    
    public interface ActionListener {
        void onDownloadClick(FileItem item);
        void onRestoreClick(FileItem item);
        void onDeleteClick(FileItem item);
    }
    
    
    private FolderClickListener folderClickListener;
    private ActionListener actionListener;
    private boolean isFolder = false;
    private String folderPrefix = "";
    private FileItem currentItem;
    
    public FileRowPanel() {
    initComponents();

    setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
    setPreferredSize(new Dimension(getPreferredSize().width, 90));

    // GANTI: border dipasang ke jPanel1, bukan this
    jPanel1.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(60, 60, 60)),
        BorderFactory.createEmptyBorder(8, 8, 8, 8)
    ));

    addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (isFolder && folderClickListener != null) {
                folderClickListener.onFolderClick(folderPrefix);
            }
        }
    });
    
    btnAction.addActionListener(e -> {
        if (currentItem == null || actionListener == null) return;

        String btnText = btnAction.getText();
        if (btnText.equals("Download")) {
            actionListener.onDownloadClick(currentItem);
        } else if (btnText.equals("Restore")) {
            actionListener.onRestoreClick(currentItem);
        }
    });
    btnDelete.addActionListener(e -> {
    System.out.println("Delete diklik, currentItem=" + currentItem);
    if (currentItem != null && actionListener != null) {
        actionListener.onDeleteClick(currentItem);
    } else {
        System.out.println("actionListener null? " + (actionListener == null));
    }
});
}
    
    public void setFolderClickListener(FolderClickListener listener) {
        this.folderClickListener = listener;
    }
    
    public void setActionListener(ActionListener listener) {
        this.actionListener = listener;
    }
        
    public void setData(FileItem item, String parentPrefix) {
        isFolder = false;
        this.currentItem = item;
        setCursor(Cursor.getDefaultCursor());
        String fullName = item.getName();
        String displayName = fullName.contains("/")
            ? fullName.substring(fullName.lastIndexOf('/') + 1)
            : fullName;

        lblName.setText(displayName);
        lblDetail.setText(item.getFormattedSize() + "  .  " + item.getTimeModified());

        var icon = FileIconUtil.loadIcon(FileIconUtil.getIconPath(item.getName(), false), 32);
        lblIcon.setIcon(icon);
        lblIcon.setText("");

        btnAction.setVisible(true);
        btnDelete.setVisible(true);        
        setupTierBadge(item.getTier());
        
        if(item.getTier() == StorageTier.ARCHIVE) {
            checkArchiveStatus(fullName);
        } else {
            btnAction.setText("Download");
            btnAction.setEnabled(true);
        }
    }
    
    private void checkArchiveStatus(String objectName) {
        System.out.println("Cek status buat: " + objectName);
        btnAction.setText("Cek status...");
        btnAction.setEnabled(false);
        
        FileService fileService = new FileService();
        fileService.checkRestoreStatus(objectName, new FileService.StatusCallback() {
            @Override
            public void onStatus(String state) {
                btnAction.setEnabled(true);
                System.out.println("Status diterima: " + state);
                switch (state) {
                    case "Restored" -> btnAction.setText("Download");
                    case "Restoring" -> {
                        btnAction.setEnabled(false);
                        btnAction.setText("Restoring...");
                    }
                    default -> btnAction.setText("Restore");
                }
            }

            @Override
            public void onError(String error) {
                System.out.println("Error cek status: " + error);
                btnAction.setEnabled(true);
                btnAction.setText("Restore");
            }
        });
    }
    
    private void setupTierBadge(StorageTier tier) {
        lblTierBadge.setVisible(true);
        lblTierBadge.setOpaque(true);
        lblTierBadge.setBorder(BorderFactory.createEmptyBorder(2,8,2,8));
        lblTierBadge.setFont(lblTierBadge.getFont().deriveFont(11f));
        
        if (tier == StorageTier.ARCHIVE){
            lblTierBadge.setText("Archive");
            lblTierBadge.setBackground(new Color(103,80,164));
            lblTierBadge.setForeground(Color.white);
        } else {
            lblTierBadge.setText("Standard");
            lblTierBadge.setBackground(new Color(66, 133, 244));
            lblTierBadge.setForeground(Color.WHITE);
        }
    }

    public void setFolderData(String folderName, String fullPrefix) {
        isFolder = true;
        this.folderPrefix = fullPrefix;
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        lblName.setText(folderName);
        lblDetail.setText("Folder");
        
        var icon = FileIconUtil.loadIcon(FileIconUtil.getIconPath(folderName, true), 32);
        lblIcon.setIcon(icon);
        lblIcon.setText("");
        
        btnAction.setVisible(false);
        btnDelete.setVisible(false);
        lblTierBadge.setVisible(false);
    }
    
    public void setBackData(String parentPrefix) {
        isFolder = true;
        this.folderPrefix = parentPrefix;
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        lblName.setText("...");
        lblDetail.setText("Kembali");

        var icon = FileIconUtil.loadIcon("/icons/folder.svg", 32);
        lblIcon.setIcon(icon);
        lblIcon.setText("");
        btnAction.setVisible(false);
        btnDelete.setVisible(false);
        lblTierBadge.setVisible(false);
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lblIcon = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        lblDetail = new javax.swing.JLabel();
        lblTierBadge = new javax.swing.JLabel();
        btnAction = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();

        lblIcon.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        lblIcon.setText("lblIcon");

        lblName.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblName.setText("lblName");

        lblDetail.setText("lblDetail");

        lblTierBadge.setFont(new java.awt.Font("Segoe UI", 0, 10)); // NOI18N
        lblTierBadge.setText("lblTierBadge");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addComponent(lblIcon, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDetail)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblName)
                        .addGap(18, 18, 18)
                        .addComponent(lblTierBadge)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblName)
                            .addComponent(lblTierBadge))
                        .addGap(18, 18, 18)
                        .addComponent(lblDetail))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(lblIcon)))
                .addContainerGap())
        );

        btnAction.setText("Action");

        btnDelete.setText("Delete");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAction)
                    .addComponent(btnDelete))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnAction)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDelete)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAction;
    private javax.swing.JButton btnDelete;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblDetail;
    private javax.swing.JLabel lblIcon;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblTierBadge;
    // End of variables declaration//GEN-END:variables
}
