package com.freewayemi.merchant.commons.entity;

import com.freewayemi.merchant.commons.type.SettlementCycleEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SettlementConfig {

    private SettlementCycleEnum settlementCycle;
    private LyraPgSettlementConfig lyraPgSettlementConfig;
    private Boolean excludeMdrAndGstCharges;
    private String settlementType;
    private String settlementDocumentsStatus;
}
