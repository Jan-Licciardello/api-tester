package com.ntt.apitester.service.of;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ntt.apitester.dto.of.GeoCodeNetworkItemsRequestV2DTO;
import com.ntt.apitester.enums.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.ntt.apitester.utils.Utils.*;
import static java.lang.System.out;

@Component
@Slf4j
public class GeoCodeV2Test {

    private String host = "https://claawiopaa11.aws.openfiber.it";
    private String endPoint = "/networkitems/v2/geo/code";
    private String serviceName = "networkitems";
    private String companyClient = "open-fiber";

    private Boolean immagazzinaValori = false; // TODO <---------------- VARIABILE PER CAMBIARE DA SCRITTURA REQUEST CORRETTE A TEST

    private List<GeoCodeNetworkItemsRequestV2DTO> requestsToTest = Arrays.asList(
            GeoCodeNetworkItemsRequestV2DTO.builder()
                        .offset(20000)
                        .page(0)
                        .minX(12.764905764404725)
                        .minY(42.83307442173717)
                        .maxX(12.769098057095002)
                        .maxY(42.83445970026592)
                        .zoom(19.0)
                    .build()
    );

    private Map<String, String> headers = Map.of(
            "Content-Type", "application/json",
            "Authorization", "none",
            "Cookie", "visid_incap_2847014=mj3PpbIvT7SRSW3/HLf0kd7LH2YAAAAAQUIPAAAAAABvhyUjDCR2TRV2kt4pxoJW; visid_incap_2856724=IUd8H1l2QKGEnkW0rZ0MWZONJ2YAAAAAQUIPAAAAAACvxqo9A/WYBNp7iDbCSxmk; visid_incap_2873423=n7JVCWP0RUi69YmZYTj2832pUGYAAAAAQUIPAAAAAACgzT9OqDFVFSZKG2ry5AsV; OptanonAlertBoxClosed=2024-05-24T14:52:12.097Z; OptanonConsent=isGpcEnabled=0&datestamp=Fri+May+24+2024+16%3A52%3A12+GMT%2B0200+(Ora+legale+dell%E2%80%99Europa+centrale)&version=202310.1.0&browserGpcFlag=0&isIABGlobal=false&hosts=&consentId=fd44ef67-e4a6-420a-aeb9-b86f86594ec3&interactionCount=1&landingPath=NotLandingPage&groups=C0001%3A1%2CC0002%3A1%2CC0004%3A1%2CC0003%3A1; _gcl_au=1.1.917552877.1716562332; _ga_MN9RXD2GKS=GS1.2.1716562332.1.0.1716562332.60.0.0; _ga_RR7XVCDZ5Q=GS1.1.1716562332.1.0.1716562332.0.0.0; _ga=GA1.1.1230364624.1716562332; _hjSessionUser_3447379=eyJpZCI6Ijc4NGVmODM2LTllMmItNTE0MC04ZDE4LTFlYWI4ZjliNmJlZCIsImNyZWF0ZWQiOjE3MjAwMTAyMDUyOTYsImV4aXN0aW5nIjpmYWxzZX0=; _ga_V2LB7WLXZM=GS1.1.1720010204.1.0.1720010206.0.0.0; visid_incap_2854641=j1HQn3OUQGKxZpvn5ibstIE4GmYAAAAAT0IPAAAAAADfXsPK04p8hilrFY/I9KSs; UserInfo=Rk9SR0VST0NLI2phbi5saWNjaWFyZGVsbG8uZXh0ZXJuYWxAb3BlbmZpYmVyLml0I0xpY2NpYXJkZWxsbyBKYW4gKEVYVCBOVFQgRGF0YSkjc2VyZi1vZi1lLXVzZXI=; X-Auth-Token=eyJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJvZi1zZXJmLWF1dGgiLCJhdWQiOiJhdWRpZW5jZV92YWx1ZSIsImV4cCI6MTcyMDQ2MzM4NSwic3ViIjoiamFuLmxpY2NpYXJkZWxsby5leHRlcm5hbEBvcGVuZmliZXIuaXQiLCJyb2xlIjoic2VyZi1vZi1lLXVzZXIifQ.UjU4RAuycIsHG--yz_5eZDUV6lMAw2beiYqUFhnpeeJabEGOC0XPAqfRugAqd4P2FH5hVDYtSfqy_EfePDrgM0lUPnGG1NUYzP2ydjU5oRwezjarJK4U7VYcBgfQHe_V3oUZxzc-kXQ0jqp4kMqf_wQJjsa5JIQk9pfvGmocw2CHDOx7y8iVh36yKDwk4AQ6jeuTx_PxN01gQnneV1rpkbZiP5s_TFr4sZxKHxJg-RoVb96Bc2qGaRwPu9Q1pllL3jAiSZEX1YAq7zJBUbNzcmBKVB1sCrS1haM9yHEswyTUKwbkRcRveOhjoVCO_Aze1JzkQm_niZA5zlCCnEhChJf2kJW2WPIKn7HUwQIGxAc5nhfYdd1b69x_FjUVhc2NXzuEUu0Ncj1cuKcyuJhxQXzbI8eG4HT6DW5jyHCJ21aUZH2yfspLjECAIv97vRXDZVRaqnAPa8-ICho1rPi0P5NbKrtqROPrkiNeCEl6-HkdTZL9rihM-qBowHWbsMm6JpzAJ7icIyFQf0aRUHlWngJiwYRktPz7972Wfs-g9L1VZ8MM3qme3lwLVwb2Vuy6Lh1En33t173O6hEnS8a0Jd5PM09BadZbCxvvSkVnkYXpfKKOCV_zW3qzbs15uPoSnyrIp9TnBOPaqC-UD6X28pTZSltbHTuBbQYCtPgvvBI"
    );

    ObjectMapper objectMapper = new ObjectMapper();

    public void test() throws Exception{
        int nScritture = 0;
        int nTest = 0;
        int nSuccessTest = 0;

        for(GeoCodeNetworkItemsRequestV2DTO element : requestsToTest){
            String jsonBody = objectMapper.writeValueAsString(element);

            String nomeFile = element.getMinX() + "_" + element.getMinY() + "_" + element.getMaxX() + "_" + element.getMaxY() + "_" + element.getZoom();
            nomeFile = nomeFile.replace("/", "-");
            String percorsoResponse = "responses//" + companyClient + "//" + serviceName + "//" + endPoint.replaceAll(serviceName, "").replace("//", "") +"//" + nomeFile + ".json";

            String responseBody = host.contains("https") ? getJsonResponseNoHttps(host + endPoint, HttpMethod.POST, headers, element) : getJsonResponse(host + endPoint, HttpMethod.POST, headers, element);
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
                try {
                    if(!valoreResponseSalvata.isEmpty()){
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
                    }else {
                        if(responseBody.isEmpty()){
                            out.println(jsonBody + " OK");
                            nSuccessTest++;
                        }else {
                            out.println(jsonBody + " WARNING DIFFERENT VALUES!");
                        }
                    }
                }catch (StringIndexOutOfBoundsException ex){
                    out.println(jsonBody + " WARNING DIFFERENT VALUES!");
                }
            }
        }


    }

}
