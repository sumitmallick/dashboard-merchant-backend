package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.commons.type.BankEnum;
import com.freewayemi.merchant.commons.type.CardTypeEnum;
import com.freewayemi.merchant.BankInterestResponse;
import com.freewayemi.merchant.bo.BankInterestBO;
import com.freewayemi.merchant.dto.MerchantAuthDto;
import com.freewayemi.merchant.dto.request.BankInterestRequest;
import com.freewayemi.merchant.service.MerchantAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class BankInterestController {

    private final Logger logger = LoggerFactory.getLogger(BankInterestController.class);

    private final BankInterestBO bankInterestBO;

    private final MerchantAuthService merchantAuthService;

    @Autowired
    public BankInterestController(BankInterestBO bankInterestBO, MerchantAuthService merchantAuthService) {
        this.bankInterestBO = bankInterestBO;
        this.merchantAuthService = merchantAuthService;
    }

    @PostMapping("/private/api/v1/bankInterest")
    public BankInterestResponse addBankInterest(@RequestBody BankInterestRequest bankInterestRequest, final HttpServletRequest request) {
        logger.info("Request received to add bank interest with params: {}", bankInterestRequest);
        MerchantAuthDto merchantAuthDto = MerchantAuthDto.builder().request(request).build();
        merchantAuthService.doAuth(merchantAuthDto);
        return bankInterestBO.createOrUpdateBankInterest(bankInterestRequest);
    }

    @GetMapping("/private/api/v1/bankInterest")
    public BankInterestResponse getBankInterest(@RequestBody BankInterestRequest bankInterestRequest, final HttpServletRequest request) {
        logger.info("Request received to get bank interest with params: {}", bankInterestRequest);
        MerchantAuthDto merchantAuthDto = MerchantAuthDto.builder().request(request).build();
        merchantAuthService.doAuth(merchantAuthDto);
        return bankInterestBO.getBankInterest(bankInterestRequest);
    }

    @GetMapping("/private/api/v1/banks")
    public List<BankEnum> getBanks(final HttpServletRequest request) {
        MerchantAuthDto merchantAuthDto = MerchantAuthDto.builder().request(request).build();
        merchantAuthService.doAuth(merchantAuthDto);
        return bankInterestBO.getBanks();
    }

    @GetMapping("/private/api/v1/cards")
    public List<CardTypeEnum> getCards(final HttpServletRequest request) {
        MerchantAuthDto merchantAuthDto = MerchantAuthDto.builder().request(request).build();
        merchantAuthService.doAuth(merchantAuthDto);
        return bankInterestBO.getCardTypes();
    }

    @GetMapping("/private/api/v1/tenures")
    public List<Integer> getEmiTenures(final HttpServletRequest request) {
        MerchantAuthDto merchantAuthDto = MerchantAuthDto.builder().request(request).build();
        merchantAuthService.doAuth(merchantAuthDto);
        return bankInterestBO.getEmiTenures();
    }
}
