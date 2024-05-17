package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateProductSkuRequest {
    private String merchantId;
    private String consumerId;
    private String productId;
    private String consumerMobile;
    private String serialNumber;
    private String modelNumber;
    private String brand;
    private String stage;
    private String transactionId;
    private String source;
    private String brandId;
    private String gst;
    private String productName;
    private Float amount;
    private Address address;
    private Integer offerTenure;
    private Boolean isSerialNoRequired;
    private String schemeId;

    @JsonCreator
    public CreateProductSkuRequest(@JsonProperty("merchantId") String merchantId,
                                   @JsonProperty("consumerId") String consumerId,
                                   @JsonProperty("productId") String productId,
                                   @JsonProperty("consumerMobile") String consumerMobile,
                                   @JsonProperty("serialNumber") String serialNumber,
                                   @JsonProperty("modelNumber") String modelNumber,
                                   @JsonProperty("brand") String brand,
                                   @JsonProperty("stage") String stage,
                                   @JsonProperty("transactionId") String transactionId,
                                   @JsonProperty("source") String source,
                                   @JsonProperty("brandId") String brandId,
                                   @JsonProperty("gst") String gst,
                                   @JsonProperty("productName") String productName,
                                   @JsonProperty("amount") Float amount,
                                   @JsonProperty("address") Address address,
                                   @JsonProperty("offerTenure") Integer offerTenure,
                                   @JsonProperty("isSerialNoRequired") Boolean isSerialNoRequired,
                                   @JsonProperty("schemeId") String schemeId) {
        this.merchantId = merchantId;
        this.consumerId = consumerId;
        this.productId = productId;
        this.consumerMobile = consumerMobile;
        this.serialNumber = serialNumber;
        this.modelNumber = modelNumber;
        this.brand = brand;
        this.stage = stage;
        this.transactionId = transactionId;
        this.source = source;
        this.brandId = brandId;
        this.gst = gst;
        this.productName = productName;
        this.amount = amount;
        this.address = address;
        this.offerTenure = offerTenure;
        this.isSerialNoRequired = isSerialNoRequired;
        this.schemeId = schemeId;
    }
 }