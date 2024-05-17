package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.freewayemi.merchant.commons.dto.Address;
import com.freewayemi.merchant.commons.dto.DocumentInfo;
import com.freewayemi.merchant.commons.dto.OfferResponse;
import com.freewayemi.merchant.commons.entity.Params;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.Agreements;
import com.freewayemi.merchant.dto.GstDetailsInfo;
import com.freewayemi.merchant.dto.PartnerData;
import com.freewayemi.merchant.dto.StoreUserInfo;
import com.freewayemi.merchant.dto.request.Account;
import com.freewayemi.merchant.entity.Brand;
import com.freewayemi.merchant.entity.MerchantUser;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static com.freewayemi.merchant.commons.utils.paymentConstants.SALES_REFERRAL_CODE;

@Data
public class MerchantUserResponse {
    private final String merchantId;
    private final String mobile;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String displayId;
    private final String shopName;
    private final String businessName;
    private final String category;
    private final String subCategory;
    private final String gst;
    private final String pan;
    private final Address address;
    private final List<OfferResponse> offers;
    private final String qrCode;
    private final String storeCode;
    private final String status;
    private final String stage;
    private final String accountNumber;
    private final String referralCode;
    private final Boolean docsSubmit;
    private final List<DocumentInfo> documents;
    private final String type;
    private final String accountIfsc;
    private final Boolean consent;
    private final Boolean dynamicOffers;
    private final Boolean festive;
    private final Boolean allowEditOffers;
    private final Boolean isSeamless;
    private final List<String> shareImages;
    private final Instant approvedDate;
    private final Boolean isInvoiceEnabled;
    private final String ownership;
    private final List<String> onboardingStates;
    private final Boolean showConvFeeSettings;
    private final Boolean isConvFee;
    private final Boolean isSubventGst;
    private final Boolean nceOnly;
    private final Boolean hasBrandProducts;
    private final String sigDocNumber;
    private final String sigDocType;
    private final String softQrCode;
    private final StoreUserInfo storeUserInfo;
    private final Boolean hideOtherProducts;
    private final Params params;
    private final String partner;
    private final String parentMerchant;
    private final List<com.freewayemi.merchant.commons.dto.PartnerInfo> partnerInfos;
    private final List<Brand> brandInfos;
    private final List<String> partners;
    private final GstDetailsInfo gstDetailsInfo;
    private final String source;
    private Agreements agreements;
    private final String masterMerchants;
    private String nextOnboardingStage;
    private List<PartnerData> partnerData;

    @JsonCreator
    public MerchantUserResponse(MerchantUser user, MerchantUser parentMerchant, List<OfferResponse> subventions,
                                Boolean hasBrandProducts, StoreUserInfo storeUserInfo, GstDetailsInfo gstDetailsInfo, List<com.freewayemi.merchant.commons.dto.PartnerInfo> partnerInfos, List<Brand> brandInfos, String mobile) {
        this.merchantId = user.getId().toString();
        this.mobile = null == mobile ? user.getMobile() : updateMobile(mobile);
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.displayId = user.getDisplayId();
        this.shopName = user.getShopName();
        this.businessName = user.getBusinessName();
        this.category = user.getCategory();
        this.subCategory = user.getSubCategory();
        this.gst = user.getGst();
        this.address = user.getAddress();
        this.pan = user.getPan();
        this.offers = null == user.getMdrs() ? null
                : Util.getOffersFromMdrs(user.getMdrs(), subventions, user.getIsConvFee());
        this.qrCode = user.getQrCode();
        this.storeCode = user.getStoreCode();
        this.status = user.getStatus();
        this.stage = user.getStage();
        this.accountNumber = null == user.getAccount() ? null : getMaskNumber(user.getAccount());
        this.referralCode = user.getReferralCode();
        this.docsSubmit = user.isDocsSubmit();
        this.documents = user.getDocuments();
        this.type = user.getType();
        this.accountIfsc = null == user.getAccount() ? null : user.getAccount().getIfsc();
        this.consent = null != user.getConsent() && user.getConsent();
        this.dynamicOffers = null != user.getDynamicOffers() && user.getDynamicOffers();
        this.festive = false;
        this.allowEditOffers =
                null != user.getReferredBy() && SALES_REFERRAL_CODE.contains(user.getReferredBy().toUpperCase())
                        && null != user.getDynamicOffers() && user.getDynamicOffers() &&
                        StringUtils.hasText(user.getDynamicOfferTemplate());
        this.isSeamless = user.getIsSeamless();
        this.shareImages = user.getShareImages() == null || user.getShareImages().size() == 0 ? Arrays.asList(
                "https://storage.googleapis.com/payment-f76df.appspot.com/share/share1.jpg",
                "https://storage.googleapis.com/payment-f76df.appspot.com/share/share2.jpg",
                "https://storage.googleapis.com/payment-f76df.appspot.com/share/share3.jpg",
                "https://storage.googleapis.com/payment-f76df.appspot.com/share/share4.jpg"
        ) : user.getShareImages();
        this.approvedDate = user.getApprovedDate();
        this.isInvoiceEnabled = user.getIsInvoiceEnabled();
        this.ownership = user.getOwnership();
        this.onboardingStates = user.getOnboardingStates();
        this.showConvFeeSettings = StringUtils.hasText(user.getCategory()) &&
                Arrays.asList("Electric 2 Wheeler", "Vehicle sales").contains(user.getCategory().toLowerCase());
        this.isConvFee = user.getIsConvFee();
        this.isSubventGst = StringUtils.hasText(user.getCategory()) &&
                Arrays.asList("education", "healthcare", "dentist", "health and personal care")
                        .contains(user.getCategory().toLowerCase());
        this.nceOnly = user.getNceOnly();
        this.hasBrandProducts = hasBrandProducts;
        this.sigDocNumber = user.getSigDocNumber();
        this.sigDocType = user.getSigDocType();
        this.softQrCode = user.getSoftQrCode();
        this.storeUserInfo = storeUserInfo;
        this.hideOtherProducts = user.getHideOtherProducts();
        this.params = user.getParams();
        this.partner = user.getPartner();
        this.parentMerchant = user.getParentMerchant();
        this.partners = (null == user.getPartners() && Util.isNotNull(getPartners(parentMerchant))) ? getPartners(parentMerchant) : user.getPartners();
        this.gstDetailsInfo = gstDetailsInfo;
        this.partnerInfos = partnerInfos;
        this.brandInfos = brandInfos;
        this.source = user.getSource();
        this.agreements = user.getAgreements();
        this.masterMerchants = user.getMasterMerchants();
    }

    private String getMaskNumber(Account account) {
        return null == account ? "NA" : null == account.getNumber() || account.getNumber().length() <= 4 ? "NA" :
                account.getNumber().substring(0, 1) + "XXXX" +
                        account.getNumber().substring(account.getNumber().length() - 3);
    }

    private String updateMobile(String mobile) {
        if (!StringUtils.isEmpty(mobile) && mobile.contains("_")) {
            mobile = mobile.split("_")[0];
        }
        return mobile;
    }

    private List<String> getPartners(MerchantUser parentMerchant) {
        if (Util.isNotNull(parentMerchant)) {
            if (Util.isNotNull(parentMerchant.getPartners())) {
                List<String> partners = parentMerchant.getPartners();
                return partners;
            }
        }
        return null;
    }
}
