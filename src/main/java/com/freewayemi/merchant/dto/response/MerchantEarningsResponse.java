package com.freewayemi.merchant.dto.response;

import com.freewayemi.merchant.entity.Earning;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MerchantEarningsResponse {
    private PocketDetails pocketDetails;
    private List<Earning> currentEarnings;
    private List<Earning> redeemedEarnings;
    private List<Earning> expiredEarnings;
    private List<Earning> scratchCards;
    private List<Earning> earnings;
}
