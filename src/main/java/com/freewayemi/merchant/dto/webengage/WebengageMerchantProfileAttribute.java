package com.freewayemi.merchant.dto.webengage;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class WebengageMerchantProfileAttribute {
    private final String city;
    private final String state;
    private final String address;
    private final Boolean notification_enabled;
    private final String approval_date;
    private final String QR_status;
    private final Boolean mobile_app_installed;
    private final String display_id;
    private final String merchant_id;
    private final String merchant_registered_name;
    private final String merchant_display_name;
    private final String store_classification;
    private final String brand_list;
    private final String merchant_status;
    private final String secondary_owner_portal_user_id;
    private final String store_category;
    private final String store_sub_category;
    private final String ownership;
    private final String merchant_registration_source;
    private final String merchant_registration_date;
    private final String type;
    private final String name;
    private final String role;
}
