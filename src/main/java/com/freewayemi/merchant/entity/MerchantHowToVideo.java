package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "merchant_how_to_videos")
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantHowToVideo extends BaseEntity {
	private String title;
	private String videoId;
	private String thumbnail;
	private String type;
}
