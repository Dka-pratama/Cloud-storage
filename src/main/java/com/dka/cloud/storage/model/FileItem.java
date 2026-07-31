/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dka.cloud.storage.model;

/**
 *
 * @author andik
 */
public class FileItem {
    private final String name;
    private final long size;
    private final String timeModified;
    private final StorageTier tier;
    private final boolean idle;
    
    public FileItem(String name, long size, String timeModified, StorageTier tier, boolean idle) {
        this.name = name;
        this.size = size;
        this.timeModified = timeModified;
        this.tier = tier;
        this.idle = idle;
    }
    
    public String getName(){
        return name;
    }
    public long getSize(){
        return size;
    }
    public String getTimeModified(){
        return timeModified;
    }
    public StorageTier getTier(){
        return tier;
    }
    public boolean isIdle(){
        return idle;
    }
    
//    Helper ukuran
    public String getFormattedSize() {
        if (size < 1024) return size + "b";
        if (size < 1024 * 1024) return String.format("%.f KB", size / 1024.0);
        if (size < 1024 * 1024 * 1024) return String.format("%.f MB", size / (1024.0 * 1024.0));
        return String.format("%.f GB", size / (1024.0 * 1024.0 * 1024.0));
    }
}
