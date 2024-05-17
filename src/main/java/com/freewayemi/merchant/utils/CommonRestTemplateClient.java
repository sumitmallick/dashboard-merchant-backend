package com.freewayemi.merchant.utils;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CommonRestTemplateClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommonRestTemplateClient.class);

    private final static int connTimeOut = 30;
    private final static int readTimeOut = 30;

    public static RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate(populateHttpComponentsClientHttpRequestFactory(connTimeOut, readTimeOut));
        restTemplate.getRequestFactory();
        return restTemplate;
    }

    public static RestTemplate getRestTemplate(int connectionTimeoutinSeconds, int readTimeoutinSeconds) {
        RestTemplate restTemplate = new RestTemplate(populateHttpComponentsClientHttpRequestFactory(connectionTimeoutinSeconds, readTimeoutinSeconds));
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
        return restTemplate;
    }

    public static RestTemplate getRestTemplate1() {
        return new RestTemplate(populateHttpComponentsClientHttpRequestFactory(connTimeOut, readTimeOut));
//        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
//        return restTemplate;
    }


    private static HttpComponentsClientHttpRequestFactory populateHttpComponentsClientHttpRequestFactory(int connectionTimeoutinSeconds, int readTimeoutinSeconds) {
        return new HttpComponentsClientHttpRequestFactory(populateCloseableHttpClient(connectionTimeoutinSeconds, readTimeoutinSeconds));
    }

    private static CloseableHttpClient populateCloseableHttpClient(int connectionTimeoutinSeconds, int readTimeoutinSeconds) {
        return HttpClients.custom().setDefaultRequestConfig(populateRequestConfig(connectionTimeoutinSeconds, readTimeoutinSeconds)).build();
    }

    private static RequestConfig populateRequestConfig(int connectionTimeoutinSeconds, int readTimeoutinSeconds) {
        return RequestConfig.custom().setConnectTimeout(connectionTimeoutinSeconds * 1000).setConnectionRequestTimeout(connectionTimeoutinSeconds * 1000).setSocketTimeout(readTimeoutinSeconds * 1000).build();
    }

}
