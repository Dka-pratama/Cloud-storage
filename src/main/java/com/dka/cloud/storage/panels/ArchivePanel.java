/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package com.dka.cloud.storage.panels;

import com.dka.cloud.storage.model.FileItem;
import com.dka.cloud.storage.model.FolderContent;
import com.dka.cloud.storage.service.FileService;
import com.dka.cloud.storage.session.RestoreManager;
import com.dka.cloud.storage.ui.DashboardForm;
import com.dka.cloud.storage.ui.UploadDialog;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import com.dka.cloud.storage.session.NotificationManager;

/**
 *
 * @author andik
 */
public class ArchivePanel extends javax.swing.JPanel {

    /**
     * Creates new form ArchivePanel
     */
    private String currentPrefix = "";
    
    public ArchivePanel() {
    initComponents();

    folderToolbarPanel1.setBreadcrumbListener(prefix -> {
        loadFolder(prefix);
    });

    folderToolbarPanel1.setUploadListener(() -> {
        java.awt.Frame parentFrame = (java.awt.Frame) SwingUtilities.getWindowAncestor(this);
        UploadDialog dialog = new UploadDialog(parentFrame, currentPrefix);
        dialog.setVisible(true);
            
        loadFolder(currentPrefix);
    });
    
    folderToolbarPanel1.setNewFolderListener(folderName -> {
        FileService fileService = new FileService();
        fileService.createFolder(currentPrefix, folderName, new FileService.UploadCallback() {
            @Override
            public void onSuccess() {
                loadFolder(currentPrefix); // refresh, folder baru muncul
            }
            @Override
            public void onError(String message) {
                JOptionPane.showMessageDialog(ArchivePanel.this, "Gagal buat folder: " + message);
            }
        });
    });
    folderToolbarPanel1.setSearchListener(keyword -> {
            if (keyword .isEmpty()){
                loadFolder(currentPrefix);
            } else {
                searchGlobal(keyword);
            }
        });
}
    
    public void loadFolder(String prefix) {
        currentPrefix = prefix;

        FileService fileService = new FileService();
        DashboardForm.getInstance().setLoading(true);
        
        fileService.listFiles(prefix, new FileService.FolderCallback() {
            @Override
            public void onSuccess(FolderContent content) {
                folderToolbarPanel1.setPath(content.getCurrentFolder());
                renderFolder(content.getSubFolders(), content.getArchiveFiles());
                
                DashboardForm.getInstance().setLoading(false);
            }

            @Override
            public void onError(String message) {
                String safeMessage = (message != null) ? message : "Tidak diketahui";
                String lower = safeMessage.toLowerCase();
                String friendly = (lower.contains("connect") || lower.contains("timeout") || lower.contains("unknownhost"))
                    ? "Tidak ada koneksi internet — gagal memuat folder Archive."
                    : "Gagal memuat folder: " + message;
                NotificationManager.getInstance().addNotification(friendly);
                
                DashboardForm.getInstance().setLoading(false);
            }
        });
    }
    
    private void renderFolder(List<String> subFolders, List<FileItem> files) {
        JPanel innerPanel = new JPanel() {
            public boolean getScrollableTracksViewportWidth() {
                return true; 
            }
        };
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));

        if (!currentPrefix.isEmpty()) {
            FileRowPanel backRow = new FileRowPanel();
            backRow.setBackData(getParentPrefix(currentPrefix));
            backRow.setFolderClickListener(this::loadFolder);
            innerPanel.add(backRow);
            innerPanel.add(createSeparator());
        }

        for (String folderPath : subFolders) {
            String folderName = folderPath.replaceAll("/$", "");
            String displayName = folderName.contains("/")
                ? folderName.substring(folderName.lastIndexOf('/') + 1)
                : folderName;

            FileRowPanel row = new FileRowPanel();
            row.setFolderData(displayName, folderPath);
            row.setFolderClickListener(this::loadFolder);
            innerPanel.add(row);
            innerPanel.add(createSeparator());
        }

        for (FileItem item : files) {
            FileRowPanel row = new FileRowPanel();
            row.setData(item, currentPrefix);
            row.setActionListener(new FileRowPanel.ActionListener() {
                @Override
                public void onDownloadClick(FileItem item) {
                    handleDownload(item);
                }

                @Override
                public void onRestoreClick(FileItem item) {
                    handleRestore(item);
                }

                @Override
                public void onDeleteClick(FileItem item) {
                    handleDelete(item);
                }
            });
            innerPanel.add(row);
            innerPanel.add(createSeparator());
        }

        scrollFileList.setViewportView(innerPanel);
        scrollFileList.revalidate();
        scrollFileList.repaint();
    }

    private String getParentPrefix(String prefix) {
        if (prefix.isEmpty()) return "";
        String trimmed = prefix.substring(0, prefix.length() - 1);
        int lastSlash = trimmed.lastIndexOf('/');
        return lastSlash == -1 ? "" : trimmed.substring(0, lastSlash + 1);
    }
    
    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(60, 60, 60));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
    
    private void handleDownload(FileItem item) {
        String objectName = item.getName();

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(item.getName()));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File saveLocation = chooser.getSelectedFile();
        FileService fileService = new FileService();

        fileService.downloadFile(objectName, saveLocation, new FileService.DownloadCallback() {
            @Override
            public void onSuccess() {
                JOptionPane.showMessageDialog(ArchivePanel.this, "Download selesai!");
            }

            @Override
            public void onError(String error) {
                JOptionPane.showMessageDialog(ArchivePanel.this, "Gagal download: " + error);
            }
        });
    }

    private void handleRestore(FileItem item) {
        String objectName = item.getName();
        RestoreManager.getInstance().startRestore(objectName);
        JOptionPane.showMessageDialog(this, "Restore dimulai untuk \"" + item.getName() + "\". Proses berjalan di background.");
    }

    private void handleDelete(FileItem item) {
        int confirm = JOptionPane.showConfirmDialog(ArchivePanel.this,
                "Yakin ingin menghapus \"" + item.getName() + "\"?",
                "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        String objectName = item.getName();
        FileService fileService = new FileService();

        fileService.deleteFile(objectName, new FileService.DeleteCallback() {
            @Override
            public void onSuccess() {
                JOptionPane.showMessageDialog(ArchivePanel.this, "File dihapus");
                loadFolder(currentPrefix); // refresh list
            }

            @Override
            public void onError(String error) {
                JOptionPane.showMessageDialog(ArchivePanel.this, "Gagal hapus: " + error);
            }
        });
    }
    
    private void searchGlobal(String keyword) {
            FileService fileService = new FileService();
            fileService.searchFiles(keyword, new FileService.SearchCallback() {
                @Override
                public void onSucces(List<FileItem> result) {
                    renderFolder(new ArrayList<>(), result);
                }

                @Override
                public void onError(String message) {
                    JOptionPane.showMessageDialog(ArchivePanel.this, message);
                }
            });
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollFileList = new javax.swing.JScrollPane();
        folderToolbarPanel1 = new com.dka.cloud.storage.component.FolderToolbarPanel();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scrollFileList)
                    .addComponent(folderToolbarPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(folderToolbarPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scrollFileList, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.dka.cloud.storage.component.FolderToolbarPanel folderToolbarPanel1;
    private javax.swing.JScrollPane scrollFileList;
    // End of variables declaration//GEN-END:variables
}
