package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.entity.LyraPgSettlementConfig;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LyraPgSettlementConfigDto {

    private final String lcoId;

    @JsonCreator
    public LyraPgSettlementConfigDto(@JsonProperty("lcoId") String lcoId) {
        this.lcoId = lcoId;
    }

    public LyraPgSettlementConfigDto(LyraPgSettlementConfig lyraPgSettlementConfig) {
        this.lcoId = lyraPgSettlementConfig != null ? lyraPgSettlementConfig.getLcoId() : null;
    }
}
