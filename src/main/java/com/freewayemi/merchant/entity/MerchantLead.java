package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.dto.OnBoardingStage;
import com.freewayemi.merchant.enums.Status;
import com.freewayemi.merchant.pojos.gst.GSTData;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "merchant_leads")
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class MerchantLead extends BaseEntity {
    private String meCode;
    private String category;
    private String email;
    private String gst;
    private String mobile;
    private String pinCode;
    private String partner;
    private List<String> leadOwnerIds;
    private Status status;
    private String stage;
    private GSTData gstData;
    private String displayId;
    private List<OnBoardingStage> stages;
}
