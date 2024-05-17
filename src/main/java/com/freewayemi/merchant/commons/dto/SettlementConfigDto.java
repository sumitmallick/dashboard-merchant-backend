package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.type.SettlementCycleEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SettlementConfigDto {

    private final SettlementCycleEnum settlementCycle;
    private final LyraPgSettlementConfigDto lyraPgSettlementConfig;

    @JsonCreator
    public SettlementConfigDto(@JsonProperty("settlementCycle") SettlementCycleEnum settlementCycle,
                               @JsonProperty("lyraPgSettlementConfig") LyraPgSettlementConfigDto lyraPgSettlementConfig) {
        this.settlementCycle = settlementCycle;
        this.lyraPgSettlementConfig = lyraPgSettlementConfig;
    }
}
