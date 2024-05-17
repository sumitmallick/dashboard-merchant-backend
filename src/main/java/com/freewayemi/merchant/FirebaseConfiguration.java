package com.freewayemi.merchant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.freewayemi.merchant.dto.FirebaseCredential;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.StorageOptions;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfiguration {
    @Bean
    public StorageOptions storageOptions(@Value("${FIREBASE_PROJECT_ID}") String projectId,
                                         @Value("${FIREBASE_TYPE}") String type,
                                         @Value("${FIREBASE_PRIVATE_KEY_ID}") String privateKeyId,
                                         @Value("${FIREBASE_PRIVATE_KEY}") String privateKey,
                                         @Value("${FIREBASE_CLIENT_EMAIL}") String clientEmail,
                                         @Value("${FIREBASE_CLIENT_ID}") String clientId,
                                         @Value("${FIREBASE_AUTH_URI}") String authUri,
                                         @Value("${FIREBASE_TOKEN_URI}") String tokenUri,
                                         @Value("${FIREBASE_AUTH_PROVIDER_X509_CERT_URL}")
                                                 String authProviderX509CertUrl,
                                         @Value("${FIREBASE_CLIENT_X509_CERT_URL}") String clientX509CertUrl) {
        try {
            FirebaseCredential firebaseCredential = FirebaseCredential.builder()
                    .type(type)
                    .project_id(projectId)
                    .private_key_id(privateKeyId)
                    .private_key(privateKey.replace("\\n", "\n"))
                    .client_email(clientEmail)
                    .client_id(clientId)
                    .auth_uri(authUri)
                    .token_uri(tokenUri)
                    .auth_provider_x509_cert_url(authProviderX509CertUrl)
                    .client_x509_cert_url(clientX509CertUrl)
                    .build();

            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(firebaseCredential);

            assert firebaseCredential != null;
            return StorageOptions.newBuilder()
                    .setProjectId(projectId)
                    .setCredentials(GoogleCredentials.fromStream(
                            IOUtils.toInputStream(jsonString, StandardCharsets.UTF_8)))
                    .build();
        } catch (Exception e) {
            return null;
        }
    }
}
