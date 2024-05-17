package com.freewayemi.merchant.dto.sales;

import com.freewayemi.merchant.commons.dto.karza.GstAuthenticationResponse;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class GstDict {
    private String mid;
    private GstAuthenticationResponse gstAuthenticationResponse;

    private Instant lastModifiedDate;
    private Instant createdDate;
    private String gst ;
}
