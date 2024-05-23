package com.ntt.apitester.service.of;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntt.apitester.dto.of.EnrichmentRequestBody;
import com.ntt.apitester.enums.HttpMethod;
import lombok.extern.slf4j.Slf4j;
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

import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ntt.apitester.utils.Utils.*;
import static java.lang.System.out;


@Component
@Slf4j
public class Tester implements ApplicationRunner {

    ObjectMapper objectMapper = new ObjectMapper();

    // INSERIRE IN QUESTA MAPPA I VALORI CHE SI VOGGLIONO TETSARE PER ENRICHMENT
    protected Map<String, List<String>> elementToSet = Map.of(  "POP",              Arrays.asList("RM_01", "RM_07", "VE_02"),
                                                                "SEMIANELLO",       Arrays.asList("VE_02/05E", "RM_01/01W", "RM_01/01E"),
                                                                "PFP",              Arrays.asList("VE_02/15E3","VE_02/02E1", "VE_02/15E1", "VE_02/05E1"),
                                                                "PFS",              Arrays.asList("RM_01/03W11", "RM_01/01E32", "VE_02/10W11", "VE_02/11W33"),
                                                                "APPARATO_EDIFICIO",Arrays.asList("L736_VIA FRATELLI RONDINA_14", "L736_VIA MATTUGLIE_26_1"),
                                                                "PD",               Arrays.asList("VE_02/01E24/PD_001", "RM_18/03W23/PD_005"),
                                                                "EDIFICIO",         Arrays.asList("12_058_058091_8000080411_153", "05_027_027042_8000088904_49"),
                                                                "GL",               Arrays.asList("RM_18/03W/GL_0001", "RM_18/03W/GL_0002", "VE_02/15E/GL_0001", "VE_02/15E/GL_0002")
                                                                    );

    private List<Integer> levelToTest = Arrays.asList(0, -1, -2, -3, -4, -5);
    private String host = "http://localhost:8080";
    private String endPoint = "/networkitems/v1/enrich";
    private String serviceName = "networkitems";
    private String companyClient = "open-fiber";

    private Boolean immagazzinaValori = true; // VARIABILE PER CAMBIARE DA SCRITTURA REQUEST CORRETTE A TEST

    @Override
    public void run(ApplicationArguments args) throws Exception {

        int nScritture = 0;
        int nTest = 0;
        int nSuccessTest = 0;

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
//                        HttpPost httpPost = new HttpPost(host + endPoint);
//                        httpPost.setHeader("Content-Type", "application/json");
//                        httpPost.setHeader("Authorization", "Bearer YOUR_ACCESS_TOKEN");
//                        httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
                        Map<String, String> headers = Map.of(   "Content-Type", "application/json",
                                                                "Authorization", "none");

                        String nomeFile = elemnetType + "_" + name + "_" + level;
                        nomeFile = nomeFile.replace("/", "-");
                        String percorsoResponse = "responses//" + companyClient + "//" + serviceName + "//" + endPoint.replaceAll(serviceName, "").replace("//", "") +"//" + nomeFile + ".json";

                            //try (CloseableHttpResponse response = client.execute(httpPost)) {
                                // Ottenere il corpo della risposta
                                //String responseBody = new String(response.getEntity().getContent().readAllBytes());
                                String responseBody = getJsonResponse(host + endPoint, HttpMethod.POST, headers, requestBody);
                                if(immagazzinaValori) {
                                    writeJsonToFile(percorsoResponse, responseBody);
                                    nScritture++;
                                    out.println(" ----------------------------------- ");
                                }else {
                                    String valoreResponseSalvata = null;
                                    try {
                                        valoreResponseSalvata = readFileAsString(percorsoResponse);
                                    }catch (NoSuchFileException ex){
                                        log.error("\n ATTENZIONE PRIMA DI TESTARE LA REQUEST " + jsonBody + " DEVI PRIMA SALVARE IL RISULATAO IN UN FILE CON LA VARIABILE immagazzinaValori A TRUE \n");
                                        continue;
                                    }

                                    boolean areEquals = false;

                                    if(valoreResponseSalvata.trim().charAt(0) != responseBody.trim().charAt(0)){
                                        // Caso in cui un un'oggetto salavto è un array un oggetto salvato è una un oggetto json
                                    }else if(valoreResponseSalvata.startsWith("[")){
                                        JSONArray jsonResponseSalvata = new JSONArray(valoreResponseSalvata);
                                        JSONArray jsonResponseApi = new JSONArray(responseBody);
                                        areEquals = areJsonArraysEquals(jsonResponseSalvata, jsonResponseApi);
                                    }else if(valoreResponseSalvata.startsWith("{")){
                                        JSONObject jsonResponseSalvata = new JSONObject(valoreResponseSalvata);
                                        JSONObject jsonResponseApi = new JSONObject(responseBody);
                                        areEquals = areJsonObjectsEquals(jsonResponseSalvata, jsonResponseApi);
                                    }
                                    nTest++;
                                    if(areEquals){
                                        out.println(jsonBody + " OK");
                                        nSuccessTest++;
                                    }else{
                                        out.println(jsonBody + " WARNING DIFFERENT VALUES!");
                                    }

                                }

                           // }

                    }
                }
            }
        }

        if(immagazzinaValori)
            out.println("\n \nREQUEST SALVATE: " + nScritture);
        else
            out.println("\n \nTEST PASSATI: " + nSuccessTest + "/" + nTest);
    }
}
