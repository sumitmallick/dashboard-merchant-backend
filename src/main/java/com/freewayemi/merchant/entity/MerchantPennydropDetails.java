package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.dto.BankAccount.BankAccountAuthResp;
import com.freewayemi.merchant.dto.sales.PennyDropResult;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "merchant_pennydrop_details")
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class MerchantPennydropDetails extends BaseEntity {
    private String merchantId;
    private PennyDropResult pennyDropResult;
    private BankAccountAuthResp bankAccountAuthResp;
    private String acc;
    private String ifsc;

}
