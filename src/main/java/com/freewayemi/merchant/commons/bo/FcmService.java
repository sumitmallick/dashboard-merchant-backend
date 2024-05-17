package com.freewayemi.merchant.commons.bo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freewayemi.merchant.commons.utils.paymentConstants;
import com.freewayemi.merchant.commons.exception.FreewayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class FcmService {
	private static final Logger LOGGER = LoggerFactory.getLogger(FcmService.class);
	private final String serverKey;
	private final Boolean isProduction;

	@Autowired
	public FcmService(@Value("${fcm.server.key}") String serverKey, @Value("${payment.deployment.env}") String env) {
		this.serverKey = serverKey;
		this.isProduction = paymentConstants.PRODENV.equals(env);
	}

	public boolean validate_old(String fcmToken) {
		LOGGER.info("FcmToken: {}", fcmToken);
		return true;
	}

	public boolean validate(String fcmToken) {
//		if (!isProduction)
//			return true;
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "key=" + serverKey);

		String request = String.format("{\"registration_ids\":[\"%s\"], \"dry_run\": true}", fcmToken);
		HttpEntity<String> entity = new HttpEntity<>(request, headers);
		try {
			String url = "https://fcm.googleapis.com/fcm/send";
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, Object> jsonMap = objectMapper.readValue(response.getBody(),
						new TypeReference<Map<String, Object>>() {
						});
				Integer failure = (Integer) jsonMap.get("failure");
				if (failure.equals(0)) {
					return true;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Exception occurred while FCM data: ", e);
			throw new FreewayException(e.getMessage());
		}
		throw new FreewayException("Error 101");
	}
}
