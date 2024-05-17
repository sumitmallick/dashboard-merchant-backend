package com.freewayemi.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.freewayemi.merchant.commons.dto.MarginMoneyConfigDto;
import com.freewayemi.merchant.commons.type.EMIOfferType;
import com.freewayemi.merchant.entity.Brand;
import lombok.Data;

import java.util.List;

@Data
public class BrandResponse {
    private String brandId;
    private String name;
    private String emiOption;
    private String icon;
    private String sideBanner;
    private String displayHeader;
    private String displaySubHeader;
    private Boolean hideOtherProducts;
    private Boolean hideSerialNumber;
    private String category;
    private String brandType;
    private List<String> productCategories;
    private Boolean isNoCostEmi;
    private Boolean isOfferAvailable;
    /*EMI Offer type is to control what type emi offers to be shown to user while transacting
     * ALL => NoCost-LowCost-Standard offers will be shown
     * NO_COST => NoCost  offers will be shown
     * STANDARD => Standard  offers will be shown
     * LOW_COST => LowCost  offers will be shown
     * SUBVENTED => NoCost-LowCost  offers will be shown
     * */
    private EMIOfferType emiOfferType;
    private String brandDisplayId;

    private MarginMoneyConfigDto marginMoneyConfig;
    private Boolean freezePaymentModeOnSerialNumber;
    private Boolean barcodeScanEnabled;
    private Boolean hideProductAmount;

    public BrandResponse(String brandId) {
        this.brandId = brandId;
    }

    public BrandResponse(Brand brand) {
        this.brandId = String.valueOf(brand.getId());
        this.name = brand.getName();
        this.emiOption = brand.getEmiOption();
        this.icon = brand.getIcon();
        this.sideBanner = brand.getSideBanner();
        this.displayHeader = brand.getDisplayHeader();
        this.displaySubHeader = brand.getDisplaySubHeader();
        this.hideOtherProducts = brand.getHideOtherProducts();
        this.hideSerialNumber = brand.getHideSerialNumber();
        this.category = brand.getCategory();
        this.brandType = brand.getBrandType();
        this.productCategories = brand.getProductCategories();
        this.emiOfferType = brand.getEmiOfferType();
        this.brandDisplayId = brand.getBrandDisplayId();
        this.marginMoneyConfig = new MarginMoneyConfigDto(brand.getMarginMoneyConfig());
        this.freezePaymentModeOnSerialNumber = brand.getFreezePaymentModeOnSerialNumber();
        this.barcodeScanEnabled = brand.getBarcodeScanEnabled();
        this.hideProductAmount = brand.getHideProductAmount();
    }

    public BrandResponse(String id, String name){
        this.brandId=id;
        this.name=name;
    }
}
