package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.commons.bo.PaymentServiceBO;
import com.freewayemi.merchant.commons.dto.TransactionInvoiceResponse;
import com.freewayemi.merchant.bo.MerchantInvoiceBO;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.response.MerchantMonthlyInvoiceDTO;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.service.AuthCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class MerchantInvoiceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantInvoiceController.class);
    private final PaymentServiceBO paymentServiceBO;
    private final MerchantInvoiceBO merchantInvoiceBO;
    private final AuthCommonService authCommonService;
    private final MerchantUserBO merchantUserBO;

    @Autowired
    public MerchantInvoiceController(PaymentServiceBO paymentServiceBO, MerchantInvoiceBO merchantInvoiceBO,
                                     AuthCommonService authCommonService,
                                     MerchantUserBO merchantUserBO) {
        this.paymentServiceBO = paymentServiceBO;
        this.merchantInvoiceBO = merchantInvoiceBO;
        this.authCommonService = authCommonService;
        this.merchantUserBO = merchantUserBO;
    }

    @GetMapping("/api/v1/invoices")
    public List<TransactionInvoiceResponse> getTransactionInvoicesByMerchant(HttpServletRequest httpServletRequest,
                                                                             @RequestParam(value = "partner", required = false) String partner) {
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserById(merchant);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
                merchant = mu.getId().toString();
            }
            else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        return paymentServiceBO.getTransactionInvoices(merchant);
    }

    @GetMapping(value = "/api/v1/invoices/{iid}")
    public TransactionInvoiceResponse getInvoice(@PathVariable("iid") String iid, HttpServletRequest httpServletRequest,
                                                 @RequestParam(value = "partner", required = false) String partner) {
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserById(merchant);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
                merchant = mu.getId().toString();
            }
            else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        return paymentServiceBO.getTransactionInvoice(merchant, iid);
    }

    @GetMapping(value = "/api/v1/invoices/invoiceUrls")
    public List<MerchantMonthlyInvoiceDTO> getMerchantMonthlyInvoice(HttpServletRequest httpServletRequest,
                                                                     @RequestParam(value = "partner", required = false) String partner) {
        String merchant = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser mu = merchantUserBO.getUserById(merchant);
        String userMobile = mu.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = mu.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                mu = partnerUser;
                merchant = mu.getId().toString();
            }
            else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        return merchantInvoiceBO.getMerchantMonthlyInvoice(merchant);
    }
}
