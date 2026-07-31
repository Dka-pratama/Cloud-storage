/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.dikka.cloud.storage.util;

/**
 *
 * @author andik
 */
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static Properties properties;

    static {
        properties = new Properties();
        try (InputStream input = ConfigLoader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new RuntimeException("config.properties tidak ditemukan! Copy dari config.properties.example");
            }
            properties.load(input);

        } catch (IOException e) {
            throw new RuntimeException("Gagal load config: " + e.getMessage());
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }
}
