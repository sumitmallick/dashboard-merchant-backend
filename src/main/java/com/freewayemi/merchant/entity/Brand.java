package com.freewayemi.merchant.entity;

import com.freewayemi.merchant.commons.entity.BaseEntity;
import com.freewayemi.merchant.commons.entity.BrandParams;
import com.freewayemi.merchant.commons.type.EMIOfferType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "brands")
@Data
@EqualsAndHashCode(callSuper = true)
public class Brand extends BaseEntity {
    private String name;
    private String brandCode;
    private String stores;
    private String website;
    private String category;
    private String subcategory;
    private String ownedBy;
    private String secondaryOwner;
    private String createdBy;
    private String emiOption;
    private String brandId;
    private String logo;
    private String icon;
    private String sideBanner;
    private Boolean hasProducts;
    private Integer paymentCycle;
    private Boolean fraudChecks;
    private String displayHeader;
    private String displaySubHeader;
    private Boolean hideOtherProducts;
    private Boolean hideSerialNumber;
    private List<Integer> invoiceFreq;
    private Integer purchaseVelocity;
    private VelocityConfig velocityConfig;
    private Boolean fetchSecurityCredentials;
    private String brandDisplayId;
    private Boolean isBrandMdrModel;
    private String subventionType;
    private Boolean isCashbackWithoutProductEnabled;
    private String brandType;
    private List<String> productCategories;
    /*EMI Offer type is to control what type emi offers to be shown to user while transacting
     * ALL => NoCost-LowCost-Standard offers will be shown
     * NO_COST => NoCost  offers will be shown
     * STANDARD => Standard  offers will be shown
     * LOW_COST => LowCost  offers will be shown
     * SUBVENTED => NoCost-LowCost  offers will be shown
     * */
    private EMIOfferType emiOfferType;
    private BrandParams brandParams;
    private Boolean isBrandAdditionalCashbackValidationModel;

    private Float brandFeeRateInstantDiscount;

    private MarginMoneyConfig marginMoneyConfig;
    private String brandProductSku;
    private String brandAPI;
    private Boolean asyncReport;
    private Boolean asyncUnclaim;
    private Boolean scheduledUnclaim;

    // When barcodeScanEnabled flag is set as true, after selecting brand for creating transaction in merchant app,
    // barcode scanner will come up through which matching brand products will be fetched.
    private Boolean barcodeScanEnabled;

    // Hide the product amount i.e. product amount should not be pre-filled in transaction amount input box during
    // transaction journey.
    private Boolean hideProductAmount;

    private Boolean isOfferAvailable;

    private Map<String, Boolean> isPartnerOfferAvailable;

    private Boolean freezePaymentModeOnSerialNumber;
    private String defaultProductId;

}
