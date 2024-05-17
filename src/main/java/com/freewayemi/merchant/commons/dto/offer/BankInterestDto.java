package com.freewayemi.merchant.commons.dto.offer;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@JsonDeserialize(builder = BankInterestDto.BankInterestDtoBuilder.class)
@Builder(builderClassName = "BankInterestDtoBuilder", toBuilder = true)
public class BankInterestDto {
    private String merchantDisplayId;

    // use to query for brand associated txn
    private String brandDisplayId;

    List<InterestPerTenureDto> interestPerTenureDtos;

    private Boolean isActive;

    // By default applicable for all format:- dd/MM/yyyy
    private Instant validFrom;

    // By default applicable for all format:- dd/MM/yyyy
    private Instant validTo;

    @JsonPOJOBuilder(withPrefix = "")
    public static class BankInterestDtoBuilder {
    }
}
