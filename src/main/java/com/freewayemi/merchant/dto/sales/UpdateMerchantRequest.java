package com.freewayemi.merchant.dto.sales;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.dto.GoogleInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UpdateMerchantRequest {
    private String stage;
    private String email;
    private String firstName;
    private String lastName;
    @JsonProperty("owner_dob")
    private String ownerDob;
    private String shopName;
    private String businessName;
    private String type;
    private String category;
    private String subCategory;
    private String mccCode;
    private String pan;
    private String ownership;
    private String externalStoreCode;
    private String websiteUrl;
    private Double latitude;
    private Double longitude;
    private String brandId;
    private List<String> brandIds;
    private String mobile;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String pincode;
    private Boolean isCurrentSame;
    private Boolean isExclusive;
    private Boolean deployedMerchandise;
    private String noDeployedReason;
    private List<String> collateralIntent;
    private List<String> selectedBrandTags;
    private Map<String, List<String>> productCategoriesMap;
    private Integer maxTenure;
    private Boolean lowCostEmi;
    private Boolean provideMdr;
    private String brandCommercial;
    private String commercialId;
    private String commercialTitle;
    private String accountNumber;
    private String ifsc;
    private String name;
    private Boolean isResubmission;
    private String droppedReason;
    private String noMerchandiseReason;
    private GoogleInfo googleInfo;
    private List<Double> coordinates;
    @JsonProperty(value = "store_user")
    private UpdateStoreUserByMerchantRequest storeUser;
    private String notes;
    private String whyNotAadhaarKyc;
    private String docNumber;
    private Boolean verified;
}
