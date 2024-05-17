package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MerchantMonthlyInvoiceDTO {
    private String month;
    private String year;
    private String key;
    private String merchantId;
    private String filename;
    private String invoiceUrl;

    @JsonCreator
    public MerchantMonthlyInvoiceDTO(@JsonProperty("month") String month,
                                     @JsonProperty("year") String year,
                                     @JsonProperty("key") String key,
                                     @JsonProperty("merchantId") String merchantId,
                                     @JsonProperty("filename") String filename,
                                     @JsonProperty("invoiceUrl") String invoiceUrl) {
        this.month = month;
        this.year = year;
        this.key = key;
        this.merchantId = merchantId;
        this.filename = filename;
        this.invoiceUrl = invoiceUrl;
    }
}