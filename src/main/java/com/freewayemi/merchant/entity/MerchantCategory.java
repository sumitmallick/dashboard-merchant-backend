package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "merchant_categories")
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantCategory extends BaseEntity {
	private String category;
	private List<String> subCategories;
	private List<SubCategory> subCategoryList;
	private Integer order;
	private String version;
}
