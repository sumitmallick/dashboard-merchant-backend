package com.freewayemi.merchant.service;

import com.freewayemi.merchant.utils.CommonRestTemplateClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Data
public class CommonPropertiesManager {

    private final RestTemplate restTemplate;
    private final Boolean isApiCallViaDiscovery;

    @Autowired
    public CommonPropertiesManager(RestTemplate restTemplate,
                                   @Value("${api.call.via.discovery}") Boolean isApiCallViaDiscovery) {
        this.restTemplate = restTemplate;
        this.isApiCallViaDiscovery = isApiCallViaDiscovery;
    }

    public RestTemplate getRestTemplate(){
        if(isApiCallViaDiscovery){
            return this.restTemplate;
        }
        return CommonRestTemplateClient.getRestTemplate();
    }
}
