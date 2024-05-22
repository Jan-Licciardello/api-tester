package com.ntt.apitester.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class Utils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Scrive una stringa in un file JSON specificato dal percorso.
     * Se il file esiste già, sovrascrive il suo contenuto.
     *
     * @param filePath il percorso del file JSON
     * @param jsonString la stringa da scrivere nel file JSON
     */
    public static void writeJsonToFile(String filePath, String jsonString) {
        Path path = Paths.get(filePath);
        try {
            // Creare le directory se non esistono
            Files.createDirectories(path.getParent());
            // Scrivere la stringa nel file, sovrascrivendo il contenuto se il file esiste già
            Files.writeString(path, jsonString);
            System.out.println("File JSON scritto con successo: " + path.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Errore nella scrittura del file JSON: " + e.getMessage());
        }
    }

    /**
     * Legge il contenuto di un file e lo restituisce come stringa.
     *
     * @param filePath il percorso del file
     * @return il contenuto del file come stringa
     * @throws IOException se si verifica un errore di I/O
     */
    public static String readFileAsString(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path);
    }

    public static boolean areJsonArraysEquals(JSONArray jsonArray1, JSONArray jsonArray2) {
        try {
            return ((JSONArray)normalize(jsonArray1)).similar(normalize(jsonArray2));
        } catch (JSONException e) {
            return false;
        }
    }

    public static boolean areJsonObjectsEquals(JSONObject jsonObject1, JSONObject jsonObject2) {
        try {
            return ((JSONObject)normalize(jsonObject1)).similar(normalize(jsonObject2));
        } catch (JSONException e) {
            return false;
        }
    }



    private static Object normalize(Object obj) throws JSONException {
        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            List<String> keys = new ArrayList<>(jsonObject.keySet());
            Collections.sort(keys);
            JSONObject sortedJson = new JSONObject();
            for (String key : keys) {
                sortedJson.put(key, normalize(jsonObject.get(key)));
            }
            return sortedJson;
        } else if (obj instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) obj;
            List<Object> sortedList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                sortedList.add(normalize(jsonArray.get(i)));
            }
            sortedList.sort(Comparator.comparing(Object::toString));
            return new JSONArray(sortedList);
        } else {
            return obj;
        }
    }

}
