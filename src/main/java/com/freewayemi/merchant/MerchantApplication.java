package com.freewayemi.merchant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, ManagementWebSecurityAutoConfiguration.class})
@ComponentScan(basePackages = "com.freewayemi")
@EnableMongoRepositories(basePackages = "com.freewayemi")
@EnableMongoAuditing
@EnableEurekaClient
public class MerchantApplication {
    public static void main(String[] args) {
        SpringApplication.run(MerchantApplication.class, args);
    }
}
