/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dka.cloud.storage.service;

import com.dka.cloud.storage.model.*;
import com.dka.cloud.storage.network.ApiClient;

import com.google.gson.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.SwingUtilities;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author andik
 */
public class FileService extends ApiClient{

    public interface FolderCallback {
        void onSuccess(FolderContent content);
        void onError(String message);
}
    public interface RecentFilesCallback {
        void onSuccess(List<FileItem> files);
        void onError(String message);
    }
    public interface UploadCallback {
        void onSuccess();
        void onError(String message);
    }
    public interface RestoreCallback {
        void onSuccess(String message);
        void onError(String error);
    }
    public interface StatusCallback {
        void onStatus(String archivalState);
        void onError(String error);
    }
    public interface DownloadCallback {
        void onSuccess();
        void onError(String error);
    }
    public interface DeleteCallback {
        void onSuccess();
        void onError(String error);
    }
    public interface SearchCallback {
        void onSucces(List<FileItem> result);
        void onError(String message);
    }
//    Ambil semua File
    public void listFiles(String folderPrefix, FolderCallback callback) {
        new Thread(()-> {
            try {
                String encodedName = URLEncoder.encode(folderPrefix, StandardCharsets.UTF_8)
                    .replace("+", "%20");
                ApiResponse response = get("/storage/list?folder=" + encodedName);
                
                if(response.isSuccess()) {
                    FolderContent content = parseFolderContent(response.body);
                    SwingUtilities.invokeLater(()-> callback.onSuccess(content));
                } else {
                    SwingUtilities.invokeLater(()-> callback.onError("Gagal : Http" + response.statusCode));
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(()-> callback.onError(e.getMessage()));
            }
        }).start();
    }
    private FolderContent parseFolderContent(String jsonBody) {
    JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();

    String currentFolder = json.has("currentFolder") ? json.get("currentFolder").getAsString() : "";

    List<String> subFolders = new ArrayList<>();
    if (json.has("subFolder") && !json.get("subFolder").isJsonNull()) {
        JsonArray subFolderArray = json.getAsJsonArray("subFolder");
        for (var el : subFolderArray) {
            subFolders.add(el.getAsString());
        }
    }

    List<FileItem> standardFiles = new ArrayList<>();
    if (json.has("object") && !json.get("object").isJsonNull()) {
        standardFiles = parseFileArray(json.getAsJsonObject("object").getAsJsonArray("files"));
    }

    List<FileItem> archiveFiles = new ArrayList<>();
    if (json.has("archive") && !json.get("archive").isJsonNull()) {
        archiveFiles = parseFileArray(json.getAsJsonObject("archive").getAsJsonArray("files"));
    }

    String nextStartWith = null;
    if (json.has("nextStartWith") && !json.get("nextStartWith").isJsonNull()) {
        nextStartWith = json.get("nextStartWith").getAsString();
    }

    return new FolderContent(currentFolder, subFolders, standardFiles, archiveFiles, nextStartWith);
}
//    File terbaru
    public void getRecentFiles(int limit, RecentFilesCallback callback) {
    new Thread(() -> {
        try {
            ApiResponse response = get("/storage/list");
            System.err.println(response.body);
            
            if (response.isSuccess()) {
                FolderContent content = parseFolderContent(response.body);

                // Gabung Standard + Archive jadi satu, biar recent-nya lintas tier
                List<FileItem> allFiles = new ArrayList<>();
                allFiles.addAll(content.getStandardFiles());
                allFiles.addAll(content.getArchiveFiles());

                List<FileItem> recentFiles = allFiles.stream()
                        .sorted((a, b) -> b.getTimeModified().compareTo(a.getTimeModified()))
                        .limit(limit)
                        .collect(Collectors.toList());

                SwingUtilities.invokeLater(() -> callback.onSuccess(recentFiles));
            } else {
                SwingUtilities.invokeLater(() ->
                    callback.onError("Gagal: HTTP " + response.statusCode));
            }
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> callback.onError(e.getMessage()));
        }
    }).start();
}

    public void uploadFile(File file, String folder, String customName, String tier, UploadCallback callback) {
    new Thread(() -> {
        try {
            byte[] fileBytes = java.nio.file.Files.readAllBytes(file.toPath());

            // Gabungkan folder + nama file jadi objectName lengkap
            String objectName = folder.isEmpty() ? customName : folder + customName;

            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(BASE_URL + "/storage/upload"))
                    .header("Authorization", "Bearer " + com.dka.cloud.storage.session.Session.getInstance().getToken())
                    .header("x-file-name", objectName)
                    .header("x-storage-tier", tier)
                    .header("Content-Type", "application/octet-stream")
                    .POST(java.net.http.HttpRequest.BodyPublishers.ofByteArray(fileBytes))
                    .build();

            var client = java.net.http.HttpClient.newHttpClient();
            var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                SwingUtilities.invokeLater(callback::onSuccess);
            } else {
                SwingUtilities.invokeLater(() -> callback.onError("HTTP " + response.statusCode() + ": " + response.body()));
            }
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> callback.onError(e.getMessage()));
        }
    }).start();
}
    
    public void createFolder(String parentPrefix, String folderName, UploadCallback callback) {
        new Thread(() -> {
            try {
                String objectName = parentPrefix + folderName + "/";

                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create(BASE_URL + "/storage/upload"))
                        .header("Authorization", "Bearer " + com.dka.cloud.storage.session.Session.getInstance().getToken())
                        .header("x-file-name", objectName)
                        .header("x-storage-tier", "Standard")
                        .header("Content-Type", "application/octet-stream")
                        .POST(java.net.http.HttpRequest.BodyPublishers.ofByteArray(new byte[0])) // body kosong
                        .build();

                var client = java.net.http.HttpClient.newHttpClient();
                var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    SwingUtilities.invokeLater(callback::onSuccess);
                } else {
                    SwingUtilities.invokeLater(() -> callback.onError("HTTP " + response.statusCode()));
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> callback.onError(e.getMessage()));
            }
        }).start();
    }
    
    public void restoreFile(String objectName, int hours, RestoreCallback callback) {
        new Thread(()->{
            try{
                JsonObject body = new JsonObject();
                body.addProperty("hours", hours);
                
                ApiResponse response = post("/storage/restore/" + objectName, body.toString());
                
                if(response.isSuccess()) {
                    JsonObject json = JsonParser.parseString(response.body).getAsJsonObject();
                    String message = json.get("pesan").getAsString();
                    SwingUtilities.invokeLater(()-> callback.onSuccess(message));
                } else {
                    SwingUtilities.invokeLater(()-> callback.onError("Http " + response.statusCode));
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(()-> callback.onError(e.getMessage()));
            }
        }).start();
    }
    
    public void checkRestoreStatus(String objectName, StatusCallback callback) {
        new Thread (()->{
            try {
                String encodedName = URLEncoder.encode(objectName, StandardCharsets.UTF_8)
                    .replace("+", "%20");
                ApiResponse response = get("/storage/restore-status/" + encodedName);
                
                if(response.isSuccess()) {
                    JsonObject json = JsonParser.parseString(response.body).getAsJsonObject();
                    String state = json.get("archivalState").getAsString();
                    SwingUtilities.invokeLater(()->callback.onStatus(state));
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(()->callback.onError(e.getMessage()));
            }
        }).start();
    }
    
    public void downloadFile(String objectName, File saveLocation, DownloadCallback callback) {
        new Thread(() -> {
            try {
                String encodedName = URLEncoder.encode(objectName, StandardCharsets.UTF_8)
                        .replace("+", "%20");

                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create(BASE_URL + "/storage/download/" + encodedName))
                        .header("Authorization", "Bearer " + com.dka.cloud.storage.session.Session.getInstance().getToken())
                        .GET()
                        .build();

                var client = java.net.http.HttpClient.newHttpClient();
                var response = client.send(request,
                        java.net.http.HttpResponse.BodyHandlers.ofFile(saveLocation.toPath()));
            
                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    SwingUtilities.invokeLater(callback::onSuccess);
                } else {
                    SwingUtilities.invokeLater(() -> callback.onError("HTTP " + response.statusCode()));
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> callback.onError(e.getMessage()));
            }
        }).start();
    }
    
    public void deleteFile(String objectName, DeleteCallback callback) {
    new Thread(() -> {
        try {
            String encodedName = URLEncoder.encode(objectName, StandardCharsets.UTF_8)
                    .replace("+", "%20");

            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(BASE_URL + "/storage/delete/" + encodedName))
                    .header("Authorization", "Bearer " + com.dka.cloud.storage.session.Session.getInstance().getToken())
                    .DELETE()
                    .build();

            var client = java.net.http.HttpClient.newHttpClient();
            var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                SwingUtilities.invokeLater(callback::onSuccess);
            } else {
                SwingUtilities.invokeLater(() -> callback.onError("HTTP " + response.statusCode()));
            }
        } catch (Exception e) {
            SwingUtilities.invokeLater(() -> callback.onError(e.getMessage()));
        }
    }).start();
}
    
    private List<FileItem> parseFileArray(JsonArray filesArray) {
        List<FileItem> result = new ArrayList<>();

        for (var element : filesArray) {
            JsonObject fileJson = element.getAsJsonObject();

            String name = fileJson.get("name").getAsString();
            long size = fileJson.get("size").getAsLong();
            String timeCreated = fileJson.get("timeCreated").getAsString();
            String timeModified = fileJson.get("timeModified").getAsString();
            StorageTier tier = StorageTier.fromString(fileJson.get("storageTier").getAsString());
            boolean isIdle = fileJson.get("isIdle").getAsBoolean();

            result.add(new FileItem(name, size, timeCreated, timeModified, tier, isIdle));
        }

        return result;
    }
    
    public void searchFiles(String keyword, SearchCallback callback) {
        new Thread(()->{
            try {
                String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
                ApiResponse response = get("/storage/search?q=" + encodedKeyword);
                
                if(response.isSuccess()) {
                    List<FileItem> result = parseSearchResults(response.body);
                    SwingUtilities.invokeLater(()->callback.onSucces(result));
                } else {
                    SwingUtilities.invokeLater(() -> callback.onError("HTTP " + response.statusCode));
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(()-> callback.onError(e.getMessage()));
            }
        }).start();
    }
    
    private List<FileItem> parseSearchResults(String jsonBody) {
        List<FileItem> results = new ArrayList<>();
        
        JsonObject json = JsonParser.parseString(jsonBody).getAsJsonObject();
        JsonArray resultsArray = json.getAsJsonArray("result");
        
        for (var element: resultsArray) {
            JsonObject fileJson = element.getAsJsonObject();
            
            String name = fileJson.get("name").getAsString();
            long size = fileJson.get("size").getAsLong();
            String timeCreated = fileJson.get("timeModified").getAsString();
            String timeModified = fileJson.get("timeModified").getAsString();
            StorageTier tier = StorageTier.fromString(fileJson.get("storageTier").getAsString());
            boolean isIdle = fileJson.has("isIdle") && fileJson.get("isIdle").getAsBoolean();
            
            results.add(new FileItem(name, size, timeCreated, timeModified, tier, isIdle));
        }
        
        return results;
    }
}