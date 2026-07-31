/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dka.cloud.storage.network;

import java.net.http.HttpRequest;
/**
 *
 * @author andik
 */
public class StorageService extends ApiClient {
    public interface StorageCallback {
        void onSuccess(double objectPercent, double archivePercent);
        void onError(String error);
    }
    
    public void getStorageUsage(StorageCallback callback) {
        new Thread(()->{
            try {
                ApiResponse response = get("/storage/usage");
                
                if(response.isSuccess()) {
                    com.google.gson.JsonObject json = com.google.gson.JsonParser
                            .parseString(response.body)
                            .getAsJsonObject();
                    
                    var obj = json.getAsJsonObject("objectStorage");
                    var arc = json.getAsJsonObject("archiveStorage");
                    
                    double objUsage = obj.get("usedGB").getAsDouble();
                    double objTotal = obj.get("totalGB").getAsDouble();
                    double arcUsage = arc.get("usedGB").getAsDouble();
                    double arcTotal = arc.get("totalGB").getAsDouble();
                    
                    double objPercent = (objUsage / objTotal)* 100;
                    double arcPercent = (arcUsage / arcTotal)* 100;
                    
                    javax.swing.SwingUtilities.invokeLater(()-> 
                    callback.onSuccess(objPercent, arcPercent));
                } else {
                    javax.swing.SwingUtilities.invokeLater(()->
                    callback.onError("Gagal mengambil data : Http" + response.statusCode));
                }
            } catch (Exception e) {
                javax.swing.SwingUtilities.invokeLater(()->
                callback.onError(e.getMessage()));
            }
        }).start();
    }
}
