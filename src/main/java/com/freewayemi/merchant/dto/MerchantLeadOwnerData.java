package com.freewayemi.merchant.dto;
import com.freewayemi.merchant.dto.sales.SalesUserProfile;
import com.freewayemi.merchant.entity.MerchantUser;
import lombok.Data;

@Data
public class MerchantLeadOwnerData {
    public MerchantUser merchantUser;
    public SalesUserProfile salesUserProfile;

    public MerchantLeadOwnerData(MerchantUser merchantUser, SalesUserProfile salesUserProfile) {
        this.merchantUser = merchantUser;
        this.salesUserProfile = salesUserProfile;
    }
}
