package com.freewayemi.merchant.commons.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@JsonDeserialize(builder = DpChargeInfo.DpChargeInfoBuilder.class)
@Builder(builderClassName = "DpChargeInfoBuilder", toBuilder = true)
@ToString
public class DpChargeInfo {
    private Float downPaymentAmount;
    private Float processingFee;
    private Float gstOnProcessingFee;

    @JsonPOJOBuilder(withPrefix = "")
    public static class DpChargeInfoBuilder {
    }

}
