package com.freewayemi.merchant.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.dto.Address;
import com.freewayemi.merchant.commons.dto.OfferResponse;
import lombok.Data;

import java.util.List;

@Data
public class ProfileRequest {
    private final String shopName;
    private final String category;
    private final String subCategory;
    private final String mccCode;
    private final String gst;
    private final String pan;
    private final Address address;
    private final Account account;
    private final List<OfferResponse> offers;
    private final String ownership;
    private final String qrCode;
    private final Boolean consent;
    private final Boolean activateInvoice;
    private final String firstName;
    private final String lastName;
    private final String businessName;
    private final String sigDocType;
    private final String sigDocNumber;
    private final String displayName;
    private final String url;
    private final Boolean docsSubmitted;
    private final Boolean accountSubmitted;
    private final String email;
    private final String referralCode;
    private final String stage;
    @JsonCreator
    public ProfileRequest(@JsonProperty("shopName") String shopName,
                          @JsonProperty("category") String category,
                          @JsonProperty("subCategory") String subCategory,
                          @JsonProperty("mccCode") String mccCode,
                          @JsonProperty("gst") String gst,
                          @JsonProperty("pan") String pan,
                          @JsonProperty("ownership") String ownership,
                          @JsonProperty("address") Address address,
                          @JsonProperty("account") Account account,
                          @JsonProperty("qrCode") String qrCode,
                          @JsonProperty("offers") List<OfferResponse> offers,
                          @JsonProperty("consent") Boolean consent,
                          @JsonProperty("activateInvoice") Boolean activateInvoice,
                          @JsonProperty("firstName") String firstName,
                          @JsonProperty("lastName") String lastName,
                          @JsonProperty("businessName") String businessName,
                          @JsonProperty("sigDocType") String sigDocType,
                          @JsonProperty("sigDocNumber") String sigDocNumber,
                          @JsonProperty("displayName") String displayName,
                          @JsonProperty("url") String url,
                          @JsonProperty("docsSubmitted") Boolean docsSubmitted,
                          @JsonProperty("accountSubmitted") Boolean accountSubmitted,
                          @JsonProperty("email") String email,
                          @JsonProperty("referralCode") String referralCode,
                          @JsonProperty("stage") String stage) {
        this.shopName = shopName;
        this.category = category;
        this.subCategory = subCategory;
        this.mccCode = mccCode;
        this.gst = gst;
        this.pan = pan;
        this.address = address;
        this.account = account;
        this.offers = offers;
        this.ownership = ownership;
        this.qrCode = qrCode;
        this.consent = consent;
        this.activateInvoice = activateInvoice;
        this.firstName = firstName;
        this.lastName = lastName;
        this.businessName = businessName;
        this.sigDocType = sigDocType;
        this.sigDocNumber = sigDocNumber;
        this.displayName = displayName;
        this.url = url;
        this.docsSubmitted = docsSubmitted;
        this.accountSubmitted = accountSubmitted;
        this.email = email;
        this.referralCode = referralCode;
        this.stage = stage;
    }
}
