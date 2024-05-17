package com.freewayemi.merchant.dto.sales;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@Builder
public class IncentiveDetails {
    private List<MerchantIncentive> incentives;
}
