package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.dto.Construct;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "merchant_incentives")
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantIncentive extends BaseEntity {
    private String incentiveId;

    public String merchantId;
    public Instant endDate;
    public Instant startDate;
    public List<Construct> construct;
    public String constructType;

    public String name;
}
