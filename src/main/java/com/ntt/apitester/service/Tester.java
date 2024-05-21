package com.ntt.apitester.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntt.apitester.dto.EnrichmentRequestBody;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.ntt.apitester.utils.Utils.*;
import static java.lang.System.out;


@Component
public class Tester implements ApplicationRunner {

    ObjectMapper objectMapper = new ObjectMapper();


    protected Map<String, List<String>> elementToSet = Map.of("POP", Arrays.asList("RM_01", "VE_02"),
                                                            "PFP", Arrays.asList("VE_02/15E3","VE_02/02E1"));
    private List<Integer> levelToTest = Arrays.asList(0, -1, -2);
    private String host = "http://localhost:8080";
    private String endPoint = "/networkitems/v1/enrich";

    private Boolean immagazzinaValori = false; // VARIABILE PER CAMBIARE DA SCRITTURA REQUEST CORRETTE A TEST

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Fase 1
        // Se immagazina valori è true eseguo faccio una richiesta per ogni
        for(Map.Entry<String, List<String>> entry : elementToSet.entrySet()){
            String elemnetType = entry.getKey();
            for (String name : entry.getValue()){
                for(Integer level : levelToTest) {

                    try (CloseableHttpClient client = HttpClients.createDefault()) {

                        EnrichmentRequestBody requestBody = EnrichmentRequestBody.builder()
                                .elementType(elemnetType)
                                .name(name)
                                .level(level)
                                .build();

                        String jsonBody = objectMapper.writeValueAsString(requestBody);

                        // Creare la richiesta POST
                        HttpPost httpPost = new HttpPost(host + endPoint);
                        httpPost.setHeader("Content-Type", "application/json");
                        httpPost.setHeader("Authorization", "Bearer YOUR_ACCESS_TOKEN");
                        httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
                        String nomeFile = elemnetType + "_" + name + "_" + level;
                        nomeFile = nomeFile.replace("/", "-");
                        String percorsoResponse = "responses//of-networkitems//v1enrichment//" + nomeFile + ".json";

                            try (CloseableHttpResponse response = client.execute(httpPost)) {
                                // Ottenere il corpo della risposta
                                String responseBody = new String(response.getEntity().getContent().readAllBytes());
                                if(immagazzinaValori) {
                                    writeJsonToFile(percorsoResponse, responseBody);
                                    out.println(" ----------------------------------- ");
                                }else {
                                    String valoreResponseSalvata = readFileAsString(percorsoResponse);
                                    //List<Map<String, Object>> jsonResponseSalvata = objectMapper.readValue(valoreResponseSalvata, new TypeReference<List<Map<String, Object>>>() {});
                                    //List<Map<String, Object>> jsonRisposta        = objectMapper.readValue(responseBody, new TypeReference<List<Map<String, Object>>>() {});
//                                    JsonNode jsonResponseSalvata = objectMapper.readTree(valoreResponseSalvata);
//                                    JsonNode jsonResponseApi = objectMapper.readTree(responseBody);
//
                                    JSONArray jsonResponseSalvata = new JSONArray(valoreResponseSalvata);
                                    JSONArray jsonResponseApi = new JSONArray(responseBody);

                                    boolean areEquals = areJsonArrayEquals(jsonResponseSalvata, jsonResponseApi);

                                    if(areEquals)
                                        out.println(jsonBody + " OK");
                                    else
                                        out.println(jsonBody + " WARNING DIFFERENT VALUES!");

                                }

                            }

                    }
                }
            }
        }
    }
}
