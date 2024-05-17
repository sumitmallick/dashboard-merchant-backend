package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

@Data
@JsonDeserialize(builder = EligibilityDetailV2.EligibilityDetailV2Builder.class)
@Builder(builderClassName = "EligibilityDetailV2Builder", toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EligibilityDetailV2 {
    private final String bankName;
    private final String bankCode;
    private final String cardType;

    @JsonPOJOBuilder(withPrefix = "")
    public static class EligibilityDetailV2Builder {
    }
}
