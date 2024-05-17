package com.freewayemi.merchant.dto.sales;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.dto.Address;
import com.freewayemi.merchant.commons.dto.DocumentInfo;
import com.freewayemi.merchant.commons.entity.Params;
import com.freewayemi.merchant.commons.entity.PaymentProviderInfo;
import com.freewayemi.merchant.commons.entity.SettlementConfig;
import com.freewayemi.merchant.dto.gst.GstAuthResp;
import com.freewayemi.merchant.dto.request.Account;
import com.freewayemi.merchant.entity.MerchantProperties;
import com.freewayemi.merchant.enums.Status;
import com.freewayemi.merchant.pojos.gst.GSTData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MerchantInfo {
    private String _id;
    private MerchantProperties merchantProperties;
    private String mid;
    private String displayId;
    private String shopName;
    private String businessName;
    private String mobile;
    private String email;
    private String gst;
    private Address address;
    private String status;
    private String createdDate;
    private String stage;
    private Boolean isOnboarded;
    private List<DocumentInfo> documents;
    private Map<String, Map<String, Object>> resubmissionReason;
    private List<BrandBasicInfo> brandBasicInfos;
    private Status testTransaction;
    private Status mobileApp;
    private Status qrActivation;
    private Boolean isActivated;
    private Boolean merchandiseFileUpload;
    private String leadOwnerName;
    private String leadOwnerEmail;
    private Map<String, Status> registeredStage;
    private String category;
    private String subCategory;
    private Instant approvedDate;
    private List<PaymentProviderInfo> pgSettings;
    private TransactionDataByMerchant transactionsInfo;
    private GstAuthResp gstData;
    private Params params;
    private String type;
    private String source;
    private String firstName;
    private String lastName;
    private String mccCode;
    private String pan;
    private String ownership;
    private Account account;
    private SettlementConfig settlementConfig;
    private Integer riskCategory;

}
