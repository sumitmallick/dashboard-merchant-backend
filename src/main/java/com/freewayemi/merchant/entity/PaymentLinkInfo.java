package com.freewayemi.merchant.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaymentLinkInfo {

    private Integer expiryInMinutes;
    private List<PaymentLinkFormField> formFields;
    private Boolean sendSmsShortLink;
    private Boolean sendEmailShortLink;
    private Boolean hideOrderId;
    private Integer maxTenureOption;
    private Integer ccMaxTenureOption;
    private Integer dcMaxTenureOption;
    private Boolean showTenureOptions;
    private Boolean hasBrandProducts;

    @JsonCreator
    public PaymentLinkInfo(@JsonProperty("expiryInMinutes") Integer expiryInMinutes,
                           @JsonProperty("formFields") List<PaymentLinkFormField> formFields,
                           @JsonProperty("sendSmsShortLink") Boolean sendSmsShortLink,
                           @JsonProperty("sendEmailShortLink") Boolean sendEmailShortLink,
                           @JsonProperty("hideOrderId") Boolean hideOrderId,
                           @JsonProperty("maxTenureOption") Integer maxTenureOption,
                           @JsonProperty("ccMaxTenureOption") Integer ccMaxTenureOption,
                           @JsonProperty("dcMaxTenureOption") Integer dcMaxTenureOption,
                           @JsonProperty("showTenureOptions") Boolean showTenureOptions,
                           @JsonProperty("hasBrandProducts") Boolean hasBrandProducts) {
        this.expiryInMinutes = expiryInMinutes;
        this.formFields = formFields;
        this.sendSmsShortLink = sendSmsShortLink;
        this.sendEmailShortLink = sendEmailShortLink;
        this.hideOrderId = hideOrderId;
        this.maxTenureOption = maxTenureOption;
        this.ccMaxTenureOption = ccMaxTenureOption;
        this.dcMaxTenureOption = dcMaxTenureOption;
        this.showTenureOptions = showTenureOptions;
        this.hasBrandProducts = hasBrandProducts;
    }
}
