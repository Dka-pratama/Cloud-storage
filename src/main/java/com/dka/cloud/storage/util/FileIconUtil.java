/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dka.cloud.storage.util;

/**
 *
 * @author andik
 */

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;

public class FileIconUtil {
    public static String getIconPath(String fileName, boolean isFolder) {
        if(isFolder){
            return "/icons/folder.svg";
        }
        
        String extension = "";
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) extension = fileName.substring(lastDot + 1).toLowerCase();
        
        return switch(extension){
            case "jpg", "jpeg", "png", "gif", "webp" -> "/icons/image.svg";
            case "pdf" -> "/icons/pdf.svg";
            case "doc", "docx" -> "/icons/word.svg";
            case "xls", "xlsx" -> "/icons/excel.svg";
            case "zip", "rar", "7z" -> "/icons/zip.svg";
            case "mp4", "mkv", "avi" -> "/icons/video.svg";
            case "mp3", "wav" -> "/icons/audio.svg";
            default -> "/icons/file.svg";
        };
    }
    
    public static Icon loadIcon(String path, int size) {
        try {
            java.net.URL url = FileIconUtil.class.getResource(path);
            if (url == null) {
                System.out.println("URL null untuk: " + path);
                return null;
            }
             FlatSVGIcon icon = new FlatSVGIcon(url);
            return icon.derive(size, size);
        } catch (Exception e) {
            e.printStackTrace(); // paksa keliatan errornya
            return null;
        }
    }
}
