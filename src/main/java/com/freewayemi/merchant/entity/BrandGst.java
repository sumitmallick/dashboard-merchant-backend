package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.pojos.gst.GSTData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "brand_gst")
@Data
@EqualsAndHashCode(callSuper = true)
public class BrandGst extends BaseEntity {
    public String brandId;
    public String gst;
    public String tag;
    private String tagDeletion;
    public GSTData gstData;
}
