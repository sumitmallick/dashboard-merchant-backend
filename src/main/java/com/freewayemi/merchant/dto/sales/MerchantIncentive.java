package com.freewayemi.merchant.dto.sales;

import com.freewayemi.merchant.dto.Construct;
import lombok.Builder;
import lombok.Data;
import java.util.*;

@Data
@Builder
public class MerchantIncentive {
    private String merchantId;
    private String endDate;
    private String startDate;
    private List<Construct> construct;
    private String constructType;
    private String name;
}
