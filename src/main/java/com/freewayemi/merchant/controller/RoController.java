package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.dto.MerchantAuthDto;
import com.freewayemi.merchant.dto.RoMerchantsDetails;
import com.freewayemi.merchant.dto.request.RoCreationRequest;
import com.freewayemi.merchant.dto.response.AdminAuthUserResponse;
import com.freewayemi.merchant.dto.response.BasicResponse;
import com.freewayemi.merchant.entity.AdminAuthUser;
import com.freewayemi.merchant.service.MerchantAuthService;
import com.freewayemi.merchant.service.RoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/internal/api/v1/ro")
public class RoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoController.class);

    private final RoService roService;
    private final MerchantAuthService merchantAuthService;

    @Autowired
    public RoController(RoService roService, MerchantAuthService merchantAuthService) {
        this.roService = roService;
        this.merchantAuthService = merchantAuthService;
    }

    @GetMapping("/dashboard")
    public RoMerchantsDetails dashboard(@RequestParam String leadOwnerId) {
        return roService.dashBoard(leadOwnerId);
    }

    @PostMapping("/create")
    public BasicResponse create(@RequestBody RoCreationRequest roCreationRequest, final HttpServletRequest request) {
        LOGGER.info("Request received to create ro with params: {}", roCreationRequest);
        MerchantAuthDto merchantAuthDto = MerchantAuthDto.builder().request(request).build();
        merchantAuthService.doAuth(merchantAuthDto);
        return roService.create(roCreationRequest);
    }

    @GetMapping
    public List<AdminAuthUser> getRos(final HttpServletRequest request) {
        LOGGER.info("Request received to get all ROs");
        MerchantAuthDto merchantAuthDto = MerchantAuthDto.builder().request(request).build();
        merchantAuthService.doAuth(merchantAuthDto);
        return roService.getRosList();
    }

    @PostMapping("/updateStatus")
    public BasicResponse updateStatus(@RequestParam String mobile, @RequestParam String status,
                                      final HttpServletRequest request) {
        LOGGER.info("Request received to create ro with params: {} {}", mobile, status);
        MerchantAuthDto merchantAuthDto = MerchantAuthDto.builder().request(request).build();
        merchantAuthService.doAuth(merchantAuthDto);
        return roService.updateStatus(mobile, status);
    }

    @GetMapping("/profile")
    public AdminAuthUserResponse getRoProfile(@RequestParam("leadOwnerId") String leadOwnerId) {
        LOGGER.info("Request to get authorities");
        return roService.getProfile(leadOwnerId);
    }
}
