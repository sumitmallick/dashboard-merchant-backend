package com.freewayemi.merchant.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freewayemi.merchant.commons.dto.urlshortner.TinyOneUrlReq;
import com.freewayemi.merchant.dto.response.GeoCodeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

@Component
public class GeoCoding {
    private static final Logger LOGGER = LoggerFactory.getLogger(GeoCoding.class);
    private static final String GEOCODING_URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
    private static final String API_KEY = "AIzaSyBBKGmNjfqi04ODCQ81Iebwi26yHBzwRxA";

    private GeoCodeResponse GeocodeSync(List<Double> coordinates) throws IOException, InterruptedException {
        if (coordinates.size() < 2)
            return new GeoCodeResponse();

        String url = GEOCODING_URL + coordinates.get(0).toString()
                + "," + coordinates.get(1).toString() + "&key=" + API_KEY;

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<TinyOneUrlReq> httpEntity = new HttpEntity<>(headers);

        LOGGER.info("Request url geocoding api: " + url);
        ResponseEntity<String> responseEntity = new RestTemplate().postForEntity(url, httpEntity, String.class);
        ObjectMapper objectMapper = new ObjectMapper();

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            GeoCodeResponse response = objectMapper.readValue(responseEntity.getBody(), GeoCodeResponse.class);
            LOGGER.info("Geo Coding response is: " + response);
            return response;
        }
        return new GeoCodeResponse();
    }

    public String getPincode(List<Double> coordinates) {
        String pincode = "";
        try {
            GeoCodeResponse geoResp = GeocodeSync(coordinates);
            return geoResp.getPincode();
        }
        catch (IOException e) {
            LOGGER.error("Exception occured while fetching pincode from gecoding api", e);
        } catch (InterruptedException e) {
            LOGGER.error("Exception occured while fetching pincode from gecoding api", e);
            Thread.currentThread().interrupt();
        }
        return pincode;
    }

}
