package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "sessions")
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantSession extends BaseEntity {
    private String merchantId;
    private String user;
    private Instant lastActivityDate;
    private String version;
    private Boolean invalid;
    private String token;
    private String password;
    private String mobile;
    private Boolean mobileVerified;
    private String brandDisplayId;
}
