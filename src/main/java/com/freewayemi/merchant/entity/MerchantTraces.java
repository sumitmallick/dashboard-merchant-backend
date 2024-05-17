package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.dto.OnBoardingStage;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "merchant_traces")
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class MerchantTraces extends BaseEntity{
    private String merchantId;
    private String eventName;
    private String partner;
    private List<OnBoardingStage> onBoardingStages;
}
