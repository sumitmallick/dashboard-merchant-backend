package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.dto.request.MerchantLeadRequest;
import com.freewayemi.merchant.dto.request.MerchantLeadsRequest;
import com.freewayemi.merchant.dto.response.BasicResponse;
import com.freewayemi.merchant.entity.MerchantLead;
import com.freewayemi.merchant.service.MerchantLeadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class MerchantLeadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantLeadController.class);

    private final MerchantLeadService merchantLeadService;

    @Autowired
    public MerchantLeadController(MerchantLeadService merchantLeadService) {
        this.merchantLeadService = merchantLeadService;
    }

//    @PostMapping("/createLead")
//    public BasicResponse createLead(@RequestParam("leadOwnerId") String leadOwnerId,
//                                    @RequestBody MerchantLeadRequest merchantLeadRequest) {
//        LOGGER.info("Request received to createLead api: {} {}", leadOwnerId, merchantLeadRequest);
//        return merchantLeadService.createLead(leadOwnerId, merchantLeadRequest);
//    }
//
//    @GetMapping("/getMerchantLeads")
//    public List<MerchantLead> getMerchantLeads(@ModelAttribute MerchantLeadsRequest merchantLeadsRequest) {
//        return merchantLeadService.getMerchantLeads(merchantLeadsRequest);
//    }
//
//    @GetMapping("/getMerchantLead")
//    public MerchantLead getMerchantLead(@RequestParam String displayId) {
//        return merchantLeadService.getMerchantLeadByDisplayId(displayId);
//    }
}

