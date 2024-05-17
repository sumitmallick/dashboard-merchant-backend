package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "merchant_monthly_invoices")
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantMonthlyInvoices extends BaseEntity {
    private String merchantId;
    private String year;
    private String month;
    private String key;
    private String filename;
}
