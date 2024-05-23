package com.ntt.apitester.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntt.apitester.enums.HttpMethod;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.System.out;


public class Utils {
    private static String genericErrorMessage = "Server returned HTTP response code: 500";

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


    public static String getJsonResponse(String path, HttpMethod method, Map<String, String> headers, Object body) throws IOException {
        // Costruzione dell'URL
        URL url = new URL(path);

        // Apertura della connessione
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Impostazione del metodo di richiesta
        connection.setRequestMethod(method.name());

        // Aggiunta degli header
        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        // Aggiunta del corpo della richiesta
        if (body != null) {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(body);
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }

        // Lettura della risposta
        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        } catch (IOException e) {
            if(e.getMessage().contains(genericErrorMessage)){
                out.println("\n ---- cURL REQUEST CHE HA TORNATO 500 --- \n");
                out.println(generateCurlCommand(path, method, headers, body));
            }
            e.printStackTrace();
        } finally {
            connection.disconnect(); // Chiusura della connessione
        }

        return response.toString();
    }

    public static String generateCurlCommand(String host, HttpMethod method, Map<String, String> headers, Object body) {
        StringBuilder curlCommand = new StringBuilder("curl -X ");

        // Metodo HTTP
        curlCommand.append(method.name()).append(" ");

        // Aggiunta degli header
        if (headers != null) {
            curlCommand.append(headers.entrySet().stream()
                    .map(entry -> "-H \"" + entry.getKey() + ": " + entry.getValue() + "\"")
                    .collect(Collectors.joining(" "))).append(" ");
        }

        // Corpo della richiesta
        if (body != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonBody = objectMapper.writeValueAsString(body);
                curlCommand.append("-d '").append(jsonBody).append("' ");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        // Host
        curlCommand.append(host);

        return curlCommand.toString();
    }

}
