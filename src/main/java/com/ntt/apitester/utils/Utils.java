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

    public static boolean areJsonObjectsEqual(List<Map<String, Object>> map1, List<Map<String, Object>> map2) {
        JsonNode tree1 = objectMapper.valueToTree(map1);
        JsonNode tree2 = objectMapper.valueToTree(map2);

        return tree1.equals(tree2);
    }

    public static boolean areJsonListsEqual(List<Map<String, Object>> jsonList1, List<Map<String, Object>> jsonList2) {
        if (jsonList1 == null || jsonList2 == null) {
            return false;
        }

        if (jsonList1.size() != jsonList2.size()) {
            return false;
        }

        for (Map<String, Object> json1 : jsonList1) {
            boolean foundMatchingJson = false;
            for (Map<String, Object> json2 : jsonList2) {
                if (areJsonObjectsEqual(json1, json2)) {
                    foundMatchingJson = true;
                    break;
                }
            }
            if (!foundMatchingJson) {
                return false;
            }
        }

        return true;
    }

    private static boolean areJsonObjectsEqual(Map<String, Object> json1, Map<String, Object> json2) {
        if (json1 == null || json2 == null) {
            return false;
        }

        if (json1.size() != json2.size()) {
            return false;
        }

        for (String key : json1.keySet()) {
            if (!json2.containsKey(key)) {
                return false;
            }

            Object value1 = json1.get(key);
            Object value2 = json2.get(key);

            if (!Objects.equals(value1, value2)) {
                return false;
            }
        }

        return true;
    }

    public static boolean areJsonArrayEquals(JSONArray jsonArray1, JSONArray jsonArray2) {
        try {
            return ((JSONArray)normalize(jsonArray1)).similar(normalize(jsonArray2));
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
