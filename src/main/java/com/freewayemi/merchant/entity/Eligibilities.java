package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.bo.eligibility.EligibilityResponse;
import com.freewayemi.merchant.commons.dto.Address;
import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.commons.type.PaymentProviderEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "eligibilities")
@Data
@EqualsAndHashCode(callSuper = true)
public class Eligibilities extends BaseEntity {

    private String merchantId;
    private List<EligibilityResponse> eligibilities;
    private List<PaymentProviderEnum> supportedProviders;
    private String name;
    private String email;
    private Float amount;
    private String mobile;
    private String createdBy;
    private Boolean pl_generated;
    private Boolean success_txn;
    private String source;
    private Address address;
    private Map<String, String> customParams;
    private String otp;
    private long otpExpiry;
    private String pan;
    private String dob;
    private String annualIncome;
    private String providerGroup;

}
