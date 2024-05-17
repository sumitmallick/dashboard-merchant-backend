package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NearByMerchantRequestDTO {
    private  String latitude;
    private  String longitude;
    private  String shopName;
    private  String[] brands;
    private  Integer limit;
    private  Integer offset;
    private  Double maxDistance;

    @JsonCreator
    public NearByMerchantRequestDTO(@JsonProperty("latitude") String latitude,
                                    @JsonProperty("longitude") String longitude,
                                    @JsonProperty("shopName") String shopName,
                                    @JsonProperty("brands") String[] brands,
                                    @JsonProperty("limit") Integer limit,
                                    @JsonProperty("offset") Integer offset,
                                    @JsonProperty("maxDistance") Double maxDistance) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.shopName = shopName;
        this.brands = brands;
        this.limit = limit;
        this.offset = offset;
        this.maxDistance = maxDistance;
    }
}
