package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.MerchantConfigBO;
import com.freewayemi.merchant.dto.MerchantAuthDto;
import com.freewayemi.merchant.dto.MerchantConfigDto;
import com.freewayemi.merchant.enums.MerchantAuthSource;
import com.freewayemi.merchant.service.MerchantAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MerchantConfigController {

    private final MerchantAuthService merchantAuthService;
    private final MerchantConfigBO merchantConfigBO;

    @Autowired
    public MerchantConfigController(MerchantAuthService merchantAuthService, MerchantConfigBO merchantConfigBO) {
        this.merchantAuthService = merchantAuthService;
        this.merchantConfigBO = merchantConfigBO;
    }

    @GetMapping("/internal/api/v1/config")
    public MerchantConfigDto getMerchantConfig(@RequestParam(value = "label", required = true) String label,
                                               HttpServletRequest request) {
        merchantAuthService.authenticate(
                MerchantAuthDto.builder().request(request).source(MerchantAuthSource.INTERNAL).build());
        return merchantConfigBO.getMerchantConfigV1(label);
    }
}
