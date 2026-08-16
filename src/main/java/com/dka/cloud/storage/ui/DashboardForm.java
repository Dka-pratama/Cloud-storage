/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.dka.cloud.storage.ui;

import com.dka.cloud.storage.model.FileItem;
import com.dka.cloud.storage.model.FolderContent;
import com.dka.cloud.storage.panels.NotificationPopup;
import com.dka.cloud.storage.service.FileService;
import com.dka.cloud.storage.session.NotificationManager;
import com.dka.cloud.storage.session.RestoreManager;
import com.dka.cloud.storage.util.FileIconUtil;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import com.dka.cloud.storage.session.NotificationManager;

/**
 *
 * @author andik
 */
public class DashboardForm extends javax.swing.JFrame implements RestoreManager.Listener, NotificationManager.Listener {

    /**
     * Creates new form DashboardForm
     */
    
    private List<FileItem> allFiles = new ArrayList<>();

    private static DashboardForm instance;
    public DashboardForm() {
        initComponents();
        
    jTabbedPane1.putClientProperty("JTabbedPane.tabHeight", 40);      // tinggi tiap tab
    jTabbedPane1.putClientProperty("JTabbedPane.minimumTabWidth", 120); // lebar minimum tiap tab
    jTabbedPane1.putClientProperty("JTabbedPane.tabInsets", new Insets(8, 12, 8, 12)); // padding dalam (atas,kiri,bawah,kanan)
    
    jTabbedPane1.setIconAt(0, loadTabIcon("/icons/dashboard.svg", 18));
    jTabbedPane1.setIconAt(1, loadTabIcon("/icons/standard.svg", 18));
    jTabbedPane1.setIconAt(2, loadTabIcon("/icons/archive.svg", 18));
    jTabbedPane1.setIconAt(3, loadTabIcon("/icons/setting.svg", 18));
    jTabbedPane1.putClientProperty("JTabbedPane.tabIconPlacement", SwingConstants.LEFT);
        instance = this;
        loadFiles();
        dashboardPanel1.loadStorageUsage();
        
        standardPanel1.loadFolder("");
        archivePanel1.loadFolder("");
        
        RestoreManager.getInstance().setListener(this);
        updateRestoreIndicator();
        btnNotification.addActionListener(e -> {
            NotificationPopup popup = new NotificationPopup();
            popup.show(btnNotification, 0, btnNotification.getHeight());
            popup.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
                @Override
                public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
                    updateNotificationBadge();
                }
                @Override public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {}
                @Override public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
            });
        });
    }
    public static DashboardForm getInstance(){
        return instance;
    }
    
    public void setLoading(boolean isLoading){
        javax.swing.SwingUtilities.invokeLater(()-> {
            if(isLoading){
                ProgressBar.setIndeterminate(true);
            } else {
                ProgressBar.setIndeterminate(false);
                ProgressBar.setValue(0);
            }
        });
    }
    
    
    public void loadFiles() {
    FileService fileService = new FileService();
    DashboardForm.getInstance().setLoading(true);
    fileService.listFiles("", new FileService.FolderCallback() {
        @Override
        public void onSuccess(FolderContent content) {
            allFiles = new ArrayList<>();
            allFiles.addAll(content.getStandardFiles());
            allFiles.addAll(content.getArchiveFiles());

            refreshAllTabs();
            DashboardForm.getInstance().setLoading(false);
        }

        @Override
        public void onError(String message) {
            NotificationManager.getInstance().addNotification(friendlyError(message,"Memuat data"));
        }
    });
}
    
    private void refreshAllTabs() {
    List<FileItem> recent = allFiles.stream()
            .sorted((a, b) -> b.getTimeModified().compareTo(a.getTimeModified()))
            .limit(10)
            .collect(Collectors.toList());
    dashboardPanel1.renderFiles(recent);
}
    
    public void refreshAfterChange() {
        loadFiles();
    }
    
    private Icon loadTabIcon(String path, int size) {
    try {
        java.net.URL url = FileIconUtil.class.getResource(path);
        if (url == null) return null;
        FlatSVGIcon icon = new FlatSVGIcon(url);
        return icon.derive(size, size);
    } catch (Exception e) {
        e.printStackTrace();
        return null;
    }
}

    @Override
    public void onRestoreListChanged() {
        updateRestoreIndicator();
    }

    @Override
    public void onRestoreReady(String objectName) {
        String fileName = objectName.substring(objectName.lastIndexOf('/') + 1);
        JOptionPane.showMessageDialog(this, "\"" + fileName + "\" sudah siap didownload!");
        loadFiles(); // refresh biar tombol "Restore" berubah jadi "Download"
    }

    private void updateRestoreIndicator() {
        int restoringCount = RestoreManager.getInstance().getRestoringCount();

        if (restoringCount > 0) {
            lblRestoreStatus.setVisible(true);
            lblRestoreStatus.setText("⏳ " + restoringCount + " file sedang di-restore...");
        } else {
            lblRestoreStatus.setVisible(false);
        }
    }
    
    @Override
    public void onNotificationsChanged() {
        updateNotificationBadge();
    }
    
    private void updateNotificationBadge() {
        int unread = NotificationManager.getInstance().getUnreadCount();
        if (unread > 0) {
            lblNotifBadge.setVisible(true);
            lblNotifBadge.setText(String.valueOf(unread));
        } else {
            lblNotifBadge.setVisible(false);
        }
    }
    
    private String friendlyError(String message, String context) {
        String safeMessage = (message != null) ? message : "Tidak diketahui";
        String lower = safeMessage.toLowerCase();
        if (lower.contains("connect") || lower.contains("timeout") || lower.contains("unknownhost")) {
            return "Tidak ada koneksi internet — gagal " + context + ".";
        }
        return "Gagal " + context + ": " + message;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        settingPanel2 = new com.dka.cloud.storage.panels.SettingPanel();
        ProgressBar = new javax.swing.JProgressBar();
        jButton2 = new javax.swing.JButton();
        lblRestoreStatus = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        dashboardPanel1 = new com.dka.cloud.storage.panels.DashboardPanel();
        standardPanel1 = new com.dka.cloud.storage.panels.StandardPanel();
        archivePanel1 = new com.dka.cloud.storage.panels.ArchivePanel();
        settingPanel1 = new com.dka.cloud.storage.panels.SettingPanel();
        jLabel1 = new javax.swing.JLabel();
        btnNotification = new javax.swing.JButton();
        lblNotifBadge = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ProgressBar.setMaximumSize(new java.awt.Dimension(32767, 6));
        ProgressBar.setMinimumSize(new java.awt.Dimension(10, 6));

        jButton2.setText("Refresh");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        lblRestoreStatus.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        lblRestoreStatus.setText("lblRestoreStatus");

        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        jTabbedPane1.addTab("Dashboard", dashboardPanel1);
        jTabbedPane1.addTab("Standard", standardPanel1);
        jTabbedPane1.addTab("Archive", archivePanel1);
        jTabbedPane1.addTab("Setting", settingPanel1);

        jLabel1.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        jLabel1.setText("Status :");

        btnNotification.setText("Notif");

        lblNotifBadge.setText("lblNotifBadge");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblRestoreStatus)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 296, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnNotification)
                .addGap(18, 18, 18)
                .addComponent(lblNotifBadge)
                .addContainerGap())
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnNotification)
                    .addComponent(lblNotifBadge))
                .addGap(1, 1, 1)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 655, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(lblRestoreStatus)
                    .addComponent(ProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        loadFiles();
        dashboardPanel1.loadStorageUsage();
        standardPanel1.loadFolder("");
        archivePanel1.loadFolder("");
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DashboardForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DashboardForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DashboardForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DashboardForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DashboardForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar ProgressBar;
    private com.dka.cloud.storage.panels.ArchivePanel archivePanel1;
    private javax.swing.JButton btnNotification;
    private com.dka.cloud.storage.panels.DashboardPanel dashboardPanel1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblNotifBadge;
    private javax.swing.JLabel lblRestoreStatus;
    private com.dka.cloud.storage.panels.SettingPanel settingPanel1;
    private com.dka.cloud.storage.panels.SettingPanel settingPanel2;
    private com.dka.cloud.storage.panels.StandardPanel standardPanel1;
    // End of variables declaration//GEN-END:variables
}
