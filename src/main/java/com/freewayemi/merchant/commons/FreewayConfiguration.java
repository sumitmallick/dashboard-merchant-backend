package com.freewayemi.merchant.commons;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.freewayemi.merchant.type.MerchantConstants;
import org.apache.http.impl.NoConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class FreewayConfiguration {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        CloseableHttpClient client = HttpClients.custom()
                .setConnectionReuseStrategy(NoConnectionReuseStrategy.INSTANCE)
                .build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(client);
        factory.setConnectTimeout(MerchantConstants.CONNECTION_TIMEOUT_IN_SECONDS);
        factory.setReadTimeout(MerchantConstants.READ_TIMEOUT_IN_SECONDS);
        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }

    @Bean
    public AmazonSNS snsClient(AWSCredentials credentials, @Value("${aws.region}") String awsregion) {
        return AmazonSNSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(awsregion).build();
    }

    @Bean
    public AmazonS3 s3Client(AWSCredentials instanceCredentials, @Value("${aws.region}") String awsregion) {
        return AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(instanceCredentials))
                .withRegion(awsregion).build();
    }

    @Bean
    AmazonDynamoDB amazonDynamoDB(AWSCredentials credentials, @Value("${aws.region}") String awsregion) {
        return AmazonDynamoDBClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(awsregion).build();
    }

    @Bean
    public AWSCredentials credentials(@Value("${aws.access.key}") String awsAccessKey,
                                      @Value("${aws.secret.key}") String awsSecretKey) {
        return new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    }

    @Bean
    public AWSCredentials instanceCredentials(@Value("${aws.access.key}") String awsAccessKey,
                                              @Value("${aws.secret.key}") String awsSecretKey,
                                              @Value("${payment.deployment.env}") String env) {
//		if ("prod".equals(env)) {
//			return new InstanceProfileCredentialsProvider(true).getCredentials();
//		}
        return new BasicAWSCredentials(awsAccessKey, awsSecretKey);
    }

    @Bean
    public AWSKMSClientBuilder awskmsClientBuilder(AWSCredentials credentials,
                                                   @Value("${aws.region}") String awsregion) {
        return AWSKMSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(awsregion);
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("paymentThreadPool");
        executor.initialize();
        return executor;
    }

}
