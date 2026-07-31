/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dka.cloud.storage.network;

import javax.swing.SwingUtilities;
import com.dka.cloud.storage.model.*;
import com.google.gson.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author andik
 */
public class FileService extends ApiClient{

    public interface FileListCallback {
    void onSuccess(List<FileItem> files);
    void onError(String message);
}
    public void listFile(String folderPrefix, FileListCallback callback) {
        new Thread(()-> {
            try {
                ApiResponse response = get("/files?prefix=" + folderPrefix);
                
                if(response.isSuccess()) {
                    List<FileItem> fileList = parseFileList(response.body);
                    SwingUtilities.invokeLater(()-> callback.onSuccess(fileList));
                } else {
                    SwingUtilities.invokeLater(()-> callback.onError("Gagal : Http" + response.statusCode));
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(()-> callback.onError(e.getMessage()));
            }
        }).start();
    }


//Helper ubah Json mentah jadi List<FileItem>
private List<FileItem> parseFileList(String jsonBody) {
        List<FileItem> fileList = new ArrayList<>();

        JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();
        JsonArray filesArray = json.getAsJsonArray("files");

        for (var element : filesArray) {
            JsonObject fileJson = element.getAsJsonObject();

            String name = fileJson.get("name").getAsString();
            long size = fileJson.get("size").getAsLong();
            String timeModified = fileJson.get("timeModified").getAsString();
            StorageTier tier = StorageTier.valueOf(fileJson.get("tier").getAsString());
            boolean isIdle = fileJson.get("isIdle").getAsBoolean();

            fileList.add(new FileItem(name, size, timeModified, tier, isIdle));
        }

        return fileList;
    }
}
