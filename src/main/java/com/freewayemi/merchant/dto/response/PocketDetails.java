package com.freewayemi.merchant.dto.response;

import com.freewayemi.merchant.entity.Earning;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PocketDetails {
    private final float totalEarnings;
    private final float currentEarnings;
    private final float earningsRedeemed;
    private final float expiringEarnings;
    private final List<Earning> scratchCards;
}
