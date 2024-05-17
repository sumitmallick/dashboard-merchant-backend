package com.freewayemi.merchant.entity;


import com.freewayemi.merchant.commons.dto.Mdr;
import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "exception_brands")
@Data
@EqualsAndHashCode(callSuper = true)
public class ExceptionBrand extends BaseEntity {
    private String name;
    private String brandId;
    private List<Mdr> mdrs;
    private List<Mdr> subventions;
    private String type;
}
