package com.freewayemi.merchant.commons.dto.downpayment;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.freewayemi.merchant.commons.entity.DownPaymentConfig;
import com.freewayemi.merchant.commons.entity.DownPaymentRule;
import com.freewayemi.merchant.commons.utils.Util;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonDeserialize(builder = DownPaymentConfigDto.DownPaymentConfigDtoBuilder.class)
@Builder(builderClassName = "DownPaymentConfigDtoBuilder", toBuilder = true)
public class DownPaymentConfigDto {

    private final List<DownPaymentRulesDto> downPaymentRules;

    @JsonPOJOBuilder(withPrefix = "")
    public static class DownPaymentConfigDtoBuilder {
    }

    public static DownPaymentConfigDto populateDownPaymentConfigDto(DownPaymentConfig dpConfig) {
        if (Util.isNotNull(dpConfig)) {
            if (!CollectionUtils.isEmpty(dpConfig.getDownPaymentRules())) {
                List<DownPaymentRule> downPaymentRules = dpConfig.getDownPaymentRules();
                List<DownPaymentRulesDto> dpRulesDto = new ArrayList<>();
                for (DownPaymentRule dpRule : downPaymentRules) {
                    dpRulesDto.add(DownPaymentRulesDto.builder()
                            .cardType(dpRule.getCardType())
                            .bankCode(dpRule.getBankCode())
                            .build());
                }
                return DownPaymentConfigDto.builder().downPaymentRules(dpRulesDto).build();
            }
        }
        return null;
    }
}
