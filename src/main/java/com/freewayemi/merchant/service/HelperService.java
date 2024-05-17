package com.freewayemi.merchant.service;

import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.dto.sales.AddressComponents;
import com.freewayemi.merchant.dto.sales.PinCodeApiReponse;
import com.freewayemi.merchant.dto.sales.PinCodeResult;
import com.freewayemi.merchant.dto.sales.PincodeResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;

@Service
public class HelperService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelperService.class);

    private final String googleApiKey;

    @Autowired
    public HelperService(@Value("${google.api.key}") String googleApiKey) {
        this.googleApiKey = googleApiKey;
    }

    public PincodeResp getPincodeCity(Map<String, String> coordinates) {
        PincodeResp pincodeResp = new PincodeResp();
        pincodeResp.setCity("");
        pincodeResp.setPincCode("");
        if (coordinates.isEmpty() || coordinates.size() < 2) {
            return pincodeResp;
        }
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";
        url += coordinates.get("lat") + "," + coordinates.get("lon") + "&key=" + googleApiKey;
        LOGGER.info("pincode url : {}", url);
        try {
            HttpHeaders httpHeaders = new HttpHeaders();
            HttpEntity<String> entity = new HttpEntity<>("", httpHeaders);
            ResponseEntity<PinCodeApiReponse> response =
                    new RestTemplate().exchange(url, HttpMethod.GET, entity, PinCodeApiReponse.class);
            LOGGER.info("getPincodeCity: {}", response);
            if (response.getStatusCode().is2xxSuccessful()) {
                if(Objects.nonNull(response.getBody()) && Objects.nonNull(response.getBody().getResults())){
                    for(PinCodeResult pinCodeResult: response.getBody().getResults()){
                        if(Objects.nonNull(pinCodeResult.getAddressComponents()) && !CollectionUtils.isEmpty(pinCodeResult.getAddressComponents())){
                            for(AddressComponents addressComponents : pinCodeResult.getAddressComponents()){
                                if(addressComponents.getTypes().contains("postal_code")){
                                    pincodeResp.setPincCode(addressComponents.getLongName());
                                }
                                if(addressComponents.getTypes().contains("locality")){
                                    pincodeResp.setCity(addressComponents.getLongName());
                                }
                            }
                            if(StringUtils.hasText(pincodeResp.getCity()) && StringUtils.hasText(pincodeResp.getPincCode())){
                                return pincodeResp;
                            }
                        }
                    }
                }
                return pincodeResp;
            }
        }catch (HttpClientErrorException | HttpServerErrorException ex){
            LOGGER.error("HttpServerErrorException occurred" + ex.getResponseBodyAsString());
        } catch(Exception e) {
            LOGGER.error("Exception occurred" + e.getMessage());
        }
        return pincodeResp;
    }
}
