package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.dto.Document;
import com.freewayemi.merchant.dto.MandatoryStep;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@org.springframework.data.mongodb.core.mapping.Document(collection = "onboarding_documents")
@EqualsAndHashCode(callSuper = true)
public class OnBoardingDocument extends BaseEntity {
    private String partner;
    private String businessType;
    private String key;
    private String title;
    private Boolean isDropdown;
    private String subTitle;
    private List<Document> documents;
    private MandatoryStep isMandatory;
}
