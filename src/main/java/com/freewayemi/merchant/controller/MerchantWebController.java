package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.commons.utils.paymentConstants;
import com.freewayemi.merchant.commons.bo.NotificationService;
import com.freewayemi.merchant.commons.bo.StoreLinkServiceBO;
import com.freewayemi.merchant.commons.dto.StoreLinkDetails;
import com.freewayemi.merchant.commons.dto.StoreLinkResponse;
import com.freewayemi.merchant.commons.type.CheckoutVersion;
import com.freewayemi.merchant.commons.type.TransactionSource;
import com.freewayemi.merchant.bo.BrandBO;
import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.utils.MerchantStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MerchantWebController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantWebController.class);

    private final String baseUrl;
    private final MerchantUserBO merchantUserBO;
    private final NotificationService notificationService;
    private final StoreLinkServiceBO storeLinkServiceBO;
    private final BrandBO brandBO;
    private final String EV_MC_CODE = "5571";

    @Autowired
    public MerchantWebController(@Value("${payment.base.url}") String baseUrl, MerchantUserBO merchantUserBO,
                                 NotificationService notificationService, StoreLinkServiceBO storeLinkServiceBO,
                                 BrandBO brandBO) {
        this.baseUrl = baseUrl;
        this.merchantUserBO = merchantUserBO;
        this.notificationService = notificationService;
        this.storeLinkServiceBO = storeLinkServiceBO;
        this.brandBO = brandBO;
    }

    @RequestMapping(value = "/display/{did}")
    public ModelAndView qr(@PathVariable("did") String displayId) throws UnsupportedEncodingException {
        LOGGER.info("Received QR request for displayId: {}", displayId);
        MerchantUser mu = merchantUserBO.getMerchantUserByQR(displayId);
        if (MerchantStatus.approved.name().equals(mu.getStatus())) {
            StoreLinkResponse slr = storeLinkServiceBO.createStoreLinkV2(mu.getId().toString());
            String logo = null != mu.getParams() ? mu.getParams().getLogo() : "na.png";
            String shopName = mu.getShopName();
            String url = "redirect:" + baseUrl;
            Map<String, String> params = new HashMap<>();
            params.put("status", "checkoutVersion");
            if (null != mu.getParams() && StringUtils.hasText(mu.getParams().getCheckoutVersion()) &&
                    CheckoutVersion.V2.getVersion().equals(mu.getParams().getCheckoutVersion())) {
                url += paymentConstants.payment_STORE_URL_V3 + displayId + "?storeLinkId=" + slr.getStoreLink();
                params.put("value", CheckoutVersion.V2.getVersion());
            }else if (null != mu.getParams() && StringUtils.hasText(mu.getParams().getCheckoutVersion()) &&
                    CheckoutVersion.EDUV1.getVersion().equals(mu.getParams().getCheckoutVersion())) {
                url += paymentConstants.payment_STORE_URL_V3 + displayId + "?storeLinkId=" + slr.getStoreLink();
                params.put("value", CheckoutVersion.V2.getVersion());
            }  else if(null != mu.getParams() && StringUtils.hasText(mu.getParams().getCheckoutVersion()) &&
                    CheckoutVersion.V1.getVersion().equals(mu.getParams().getCheckoutVersion())){
                url += paymentConstants.payment_BUY_URL + "?logo=" + logo + "&shopname=" +
                        URLEncoder.encode(shopName, "UTF-8") + "&merchantId=" + mu.getId().toString() + "&source=" +
                        TransactionSource.userScannedQr.name() + "&storeLinkId=" + slr.getStoreLink() +
                        "&hasBrandProducts=" + brandBO.hasBrand(mu.getParams()) + "&isEVMerchant=" +
                        EV_MC_CODE.equalsIgnoreCase(mu.getMccCode());
                params.put("value", CheckoutVersion.V1.getVersion());
            }else {
                url += paymentConstants.payment_STORE_URL_V3 + displayId + "?storeLinkId=" + slr.getStoreLink();
                params.put("value", CheckoutVersion.V2.getVersion());
            }
            storeLinkServiceBO.patchStoreLink(slr.getStoreLink(), params);
            return new ModelAndView(url);
        }
        notificationService.sendException("QR scan for not approved merchant = " + mu.getShopName());
        return new ModelAndView("redirect:" + baseUrl + paymentConstants.payment_NA_URL);
    }

    @RequestMapping(value = "/storeLink/{id}")
    public ModelAndView storeLink(@PathVariable("id") String storeLinkId) throws UnsupportedEncodingException {
        LOGGER.info("Received request for storeLinkId: {}", storeLinkId);
        StoreLinkDetails storeLinkDetails = storeLinkServiceBO.getStoreLinkDetails(storeLinkId);
        MerchantUser mu = merchantUserBO.getUserById(storeLinkDetails.getMerchantId());
        Map<String, String> params = new HashMap<>();
        if (MerchantStatus.approved.name().equals(mu.getStatus())) {
            String logo = null != mu.getParams() ? mu.getParams().getLogo() : "na.png";
            String shopName = mu.getShopName();
            String url = "redirect:" + baseUrl + paymentConstants.payment_STORE_URL + "?logo=" + logo + "&shopname=" +
                    URLEncoder.encode(shopName, "UTF-8") + "&merchantId=" + mu.getId().toString() + "&source=" +
                    TransactionSource.storeLink.name() + "&storeLinkId=" + storeLinkId + "&mobile=" +
                    storeLinkDetails.getMobile() + "&hasBrandProducts=" + brandBO.hasBrand(mu.getParams()) +
                    "&isEVMerchant=" + EV_MC_CODE.equalsIgnoreCase(mu.getMccCode());
            params.put("status", "opened");
            storeLinkServiceBO.patchStoreLink(storeLinkId, params);
            return new ModelAndView(url);
        }
        notificationService.sendException(
                "Merchant: " + mu.getShopName() + " is not approved for doing store link transaction for id: " +
                        storeLinkId);
        params.put("status", "openingFail");
        storeLinkServiceBO.patchStoreLink(storeLinkId, params);
        return new ModelAndView("redirect:" + baseUrl + paymentConstants.payment_NA_URL);
    }

}
