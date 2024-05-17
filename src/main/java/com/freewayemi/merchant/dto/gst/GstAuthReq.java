package com.freewayemi.merchant.dto.gst;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.freewayemi.merchant.type.Source;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GstAuthReq
{
    private final String paymentRefId;
    @NotNull
    private final Source source;
    @NotEmpty
    private final String gstin;

    private final String provider;


    public GstAuthReq(String paymentRefId, Source source,String gstin, String provider) {
        this.paymentRefId = paymentRefId;
        this.source = source;
        this.gstin = gstin;
        this.provider = provider;
    }
}
