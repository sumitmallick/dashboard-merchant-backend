package com.freewayemi.merchant.dto.gst;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class BusinessDetail {
    private String descriptionOfGoods;
    private String hsnCodeOfGoods;
    private String subIndustry;
    private String industry;

    @JsonCreator
    public BusinessDetail(@JsonProperty("descriptionOfGoods") String descriptionOfGoods,
                          @JsonProperty("hsnCodeOfGoods") String hsnCodeOfGoods,
                          @JsonProperty("subIndustry") String subIndustry,
                          @JsonProperty("industry") String industry) {
        this.descriptionOfGoods = descriptionOfGoods;
        this.hsnCodeOfGoods = hsnCodeOfGoods;
        this.subIndustry = subIndustry;
        this.industry = industry;
    }
}
