package com.freewayemi.merchant.commons.bo;

import com.freewayemi.merchant.commons.dto.qr.QRRequest;
import com.freewayemi.merchant.commons.dto.qr.QRResponse;
import com.freewayemi.merchant.commons.exception.FreewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Component
public class QRService {
    private static final Logger LOGGER = LoggerFactory.getLogger(QRService.class);
    private final RestTemplate restTemplate;
    private static String qrUrl = "http://qrms/qr/api/v1/";

    @Autowired
    public QRService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public QRResponse populateQrCode(QRRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<QRRequest> entity = new HttpEntity<>(request, headers);
        try {
            String url = qrUrl + "qr";
            ResponseEntity<QRResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, QRResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return null;//response.getBody();
            }
        } catch (Exception e) {
            throw new FreewayException(e.getMessage());
        }
        throw new FreewayException("Bad Request.");
    }

    public QRResponse getQrMetadata(String qid, String partner) {
        try {
            String url = qrUrl + "metadata/" + qid;
            if (!StringUtils.isEmpty(partner)) {
                url += "?partnerCode=" + partner;
            }
            ResponseEntity<QRResponse> response = restTemplate.getForEntity(url, QRResponse.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            }
        } catch (Exception e) {
            LOGGER.error("qr error ", e);
        }
        return null;
    }
}
