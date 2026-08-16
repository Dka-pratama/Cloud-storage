/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dka.cloud.storage.model;

/**
 *
 * @author andik
 */

import java.util.List;

public class FolderContent {
    private final String currentFolder;
    private final List<String> subFolders;
    private final List<FileItem> standardFiles;
    private final List<FileItem> archiveFiles;
    private final String nextStartWith;

    public FolderContent(String currentFolder, List<String> subFolders,
                          List<FileItem> standardFiles, List<FileItem> archiveFiles,
                          String nextStartWith) {
        this.currentFolder = currentFolder;
        this.subFolders = subFolders;
        this.standardFiles = standardFiles;
        this.archiveFiles = archiveFiles;
        this.nextStartWith = nextStartWith;
    }

    public String getCurrentFolder() { return currentFolder; }
    public List<String> getSubFolders() { return subFolders; }
    public List<FileItem> getStandardFiles() { return standardFiles; }
    public List<FileItem> getArchiveFiles() { return archiveFiles; }
    public String getNextStartWith() { return nextStartWith; }
}
