package com.freewayemi.merchant.dto.sales;

import com.freewayemi.merchant.enums.Status;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class PanDict {
    private Boolean status;

    private String statusCode;
    private List<String> names;
    private String accountName;
    private String address;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String pincode;
    private Boolean anyBrandOnboarding;
    private String statusHeader;
    private String statusMsg;
    private String businessName;
    private List<String> brandIds;
    private Map<String, String> brandLogs;
    private String shopName;
}
