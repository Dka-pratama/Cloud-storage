/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dka.cloud.storage.session;

/**
 *
 * @author andik
 */

import com.dka.cloud.storage.service.FileService;
import javax.swing.Timer;
import java.util.LinkedHashMap;
import java.util.Map;

public class RestoreManager {
    private static RestoreManager instance;
    
    public enum Status { RESTORING, READY, FAILED}
    
    private final Map<String, Status> activeRestores = new LinkedHashMap<>();
    private Timer pollingTimer;
    private Listener listener;
    
    public interface Listener{
        void onRestoreListChanged();
        void onRestoreReady(String objectName);
    }
    
    private RestoreManager(){}
    
    public static RestoreManager getInstance(){
        if (instance == null) instance = new RestoreManager();
        return instance;
    }
    
    public void setListener(Listener listener) {
        this.listener = listener;
    }
    
    public void startRestore(String objectName) {
        if(activeRestores.containsKey(objectName)) return;
        
        FileService fileService = new FileService();
        fileService.restoreFile(objectName, 24, new FileService.RestoreCallback() {
            @Override
            public void onSuccess(String message) {
                activeRestores.put(objectName, Status.RESTORING);
                notifyChanged();
                ensurePollingRunning();
            }
                
            @Override
            public void onError(String error) {
                activeRestores.put(objectName, Status.FAILED);
                notifyChanged();
            }
        });
    }
    
    private void ensurePollingRunning(){
        if (pollingTimer != null && pollingTimer.isRunning()) return;
        
        pollingTimer = new Timer(120_000, e -> pollAllRestores());
        pollingTimer.start();
    }
    
    private void pollAllRestores(){
        if (activeRestores.isEmpty()) {
            pollingTimer.stop();
            return;
        }
        
        FileService fileService = new FileService();

        for (String objectName : activeRestores.keySet()) {
            if (activeRestores.get(objectName) != Status.RESTORING) continue;

            fileService.checkRestoreStatus(objectName, new FileService.StatusCallback() {
                @Override
                public void onStatus(String state) {
                    if ("Restored".equalsIgnoreCase(state)) {
                        activeRestores.put(objectName, Status.READY);
                        notifyChanged();
                        if (listener != null) listener.onRestoreReady(objectName);
                    }
                }

                @Override
                public void onError(String error) {
                    // biarin, coba lagi di polling berikutnya
                }
            });
        }
    }
    
    public Map<String, Status> getActiveRestores() {
        return activeRestores;
    }

    public int getRestoringCount() {
        return (int) activeRestores.values().stream().filter(s -> s == Status.RESTORING).count();
    }

    public void clearCompleted(String objectName) {
        activeRestores.remove(objectName);
        notifyChanged();
    }

    private void notifyChanged() {
        if (listener != null) {
            javax.swing.SwingUtilities.invokeLater(() -> listener.onRestoreListChanged());
        }
    }
}
