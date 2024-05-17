package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "partners")
@Data
@EqualsAndHashCode(callSuper = true)
public class Partner extends BaseEntity {
    public String name;
    public String partnerLogoUrl;
    public List<String> merchantAppBannerUrl;
    public List<String> stages;
}
