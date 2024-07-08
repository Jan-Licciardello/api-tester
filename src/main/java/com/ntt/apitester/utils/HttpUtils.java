package com.ntt.apitester.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class HttpUtils {

    // Classe per ignorare i controlli dei certificati
    static class TrustAllCertificates implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() { return null; }
        public void checkClientTrusted(X509Certificate[] certs, String authType) { }
        public void checkServerTrusted(X509Certificate[] certs, String authType) { }
    }

    // Metodo per disabilitare il controllo dei certificati
    private static void disableSSLVerification() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{new TrustAllCertificates()};
        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HostnameVerifier allHostsValid = (hostname, session) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    public static String getJsonResponse(String path, String method, Map<String, String> headers, Object body) throws IOException {
        try {
            // Disabilita il controllo dei certificati SSL
            disableSSLVerification();
        } catch (Exception e) {
            throw new IOException("Failed to disable SSL verification", e);
        }

        // Costruzione dell'URL
        URL url = new URL(path);

        // Apertura della connessione
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Impostazione del metodo di richiesta
        connection.setRequestMethod(method);

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
        try {
            BufferedReader in;
            if (connection.getResponseCode() >= 400) {
                in = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect(); // Chiusura della connessione
        }

        return response.toString();
    }
}

