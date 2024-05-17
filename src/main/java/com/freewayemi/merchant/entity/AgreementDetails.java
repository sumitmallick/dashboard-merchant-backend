package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.dto.Mdr;
import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "agreement_details")
@Data
@EqualsAndHashCode(callSuper = true)
public class AgreementDetails extends BaseEntity {
    private String merchantId;
    private String otp;
    private String merchantServiceAgreementUrl;
    private String merchantCommercialsAgreementUrl;
    private String merchantNtbAgreementUrl;
    private String ipAddress;
    private String latitude;
    private String longitude;
    private List<Mdr> mdrs;
    private String createdBy;
}
