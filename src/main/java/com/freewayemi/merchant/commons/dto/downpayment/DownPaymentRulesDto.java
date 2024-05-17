package com.freewayemi.merchant.commons.dto.downpayment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@JsonDeserialize(builder = DownPaymentRulesDto.DownPaymentRulesDtoBuilder.class)
@Builder(builderClassName = "DownPaymentRulesDtoBuilder", toBuilder = true)
public class DownPaymentRulesDto {

    private final String cardType;
    private final String bankCode;

    @JsonPOJOBuilder(withPrefix = "")
    public static class DownPaymentRulesDtoBuilder {
    }

}
