package com.freewayemi.merchant.dto.sales;
import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
public class GSTDetails {
    private String _id;
    private Instant createdDate;
    private Instant lastModifiedDate;
    private String merchantId;
    private String statusCode;
    private String statusMessage;
    private String source;
    private String gst;

    private GSTAuthenticationResponse gstAuthenticationResponse;
}
