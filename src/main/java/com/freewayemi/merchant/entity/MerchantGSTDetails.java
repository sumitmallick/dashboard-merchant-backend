package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.dto.karza.GstAuthReq;
import com.freewayemi.merchant.commons.dto.karza.GstAuthenticationResponse;
import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.dto.gst.GstAuthResp;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "merchant_gst_details")
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantGSTDetails extends BaseEntity {
    private String merchantId;
    private String gst;
    private GstAuthenticationResponse gstAuthenticationResponse;
    private GstAuthResp gstAuthResp;
}
