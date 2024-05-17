package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.bo.AuthUserBO;
import com.freewayemi.merchant.commons.bo.JwtTokenBO;
import com.freewayemi.merchant.commons.bo.PaymentServiceBO;
import com.freewayemi.merchant.commons.dto.BrandMerchantCredentialDTO;
import com.freewayemi.merchant.commons.dto.BrandMerchantCredentialResponse;
import com.freewayemi.merchant.commons.dto.MerchantApiCredentialsDto;
import com.freewayemi.merchant.commons.dto.PaymentConfigInfo;
import com.freewayemi.merchant.entity.Brand;
import com.freewayemi.merchant.entity.MerchantSession;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.enums.BrandMerchantCredentialStatusCode;
import com.freewayemi.merchant.utils.MerchantStatus;
import org.apache.commons.lang.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class BrandMerchantApprovalBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrandMerchantApprovalBO.class);
    private final BrandBO brandBO;
    private final MerchantUserBO merchantUserBO;
    private final MerchantSessionBO merchantSessionBO;
    private final JwtTokenBO jwtTokenBO;
    private final PaymentServiceBO paymentServiceBO;
    private final AuthUserBO authUserBO;

    @Autowired
    public BrandMerchantApprovalBO(BrandBO brandBO, MerchantUserBO merchantUserBO,
                                   MerchantSessionBO merchantSessionBO, JwtTokenBO jwtTokenBO,
                                   PaymentServiceBO paymentServiceBO, AuthUserBO authUserBO) {
        this.brandBO = brandBO;
        this.merchantUserBO = merchantUserBO;
        this.merchantSessionBO = merchantSessionBO;
        this.jwtTokenBO = jwtTokenBO;
        this.paymentServiceBO = paymentServiceBO;
        this.authUserBO = authUserBO;
    }

    public BrandMerchantCredentialResponse checkCredential(String brandDisplayId, String externalCode) {
        Brand brand = brandBO.getBrandByBrandDisplayId(brandDisplayId);
        if (BooleanUtils.isTrue(brand.getFetchSecurityCredentials())) {
            List<BrandMerchantCredentialDTO> merchantCredentialList = new ArrayList<>();
            if (StringUtils.hasText(externalCode)) {
                MerchantUser merchantUser = merchantUserBO.findByExternalStoreCode(externalCode, MerchantStatus.approved.name());
                merchantCredentialList.add(getBrandMerchantCredential(merchantUser));
            } else {
                List<MerchantUser> merchantUserList = merchantUserBO.findByBrandIdAndStatus(brand.getId().toString(), MerchantStatus.approved.name());
                if (!CollectionUtils.isEmpty(merchantUserList)) {
                    merchantUserList.forEach(merchantUser -> {
                        merchantCredentialList.add(getBrandMerchantCredential(merchantUser));
                    });
                }
            }
            if (!CollectionUtils.isEmpty(merchantCredentialList)) {
                return BrandMerchantCredentialResponse.builder()
                        .code(BrandMerchantCredentialStatusCode.SUCCESS.getCode())
                        .status(BrandMerchantCredentialStatusCode.SUCCESS.getStatus())
                        .message(BrandMerchantCredentialStatusCode.SUCCESS.getStatusMsg())
                        .merchantCredentials(merchantCredentialList)
                        .build();
            } else {
                return BrandMerchantCredentialResponse.builder()
                        .code(BrandMerchantCredentialStatusCode.FAILED_102.getCode())
                        .status(BrandMerchantCredentialStatusCode.FAILED_102.getStatus())
                        .message(BrandMerchantCredentialStatusCode.FAILED_102.getStatusMsg())
                        .build();
            }
        } else {
            return BrandMerchantCredentialResponse.builder()
                    .code(BrandMerchantCredentialStatusCode.FAILED_101.getCode())
                    .status(BrandMerchantCredentialStatusCode.FAILED_101.getStatus())
                    .message(BrandMerchantCredentialStatusCode.FAILED_101.getStatusMsg())
                    .build();
        }
    }

    private BrandMerchantCredentialDTO getBrandMerchantCredential(MerchantUser merchantUser) {
        return BrandMerchantCredentialDTO.builder()
                .externalCode(merchantUser.getParams().getExternalStoreCode())
                .storeName(merchantUser.getShopName())
                .emailId(merchantUser.getEmail())
                .gstIn(merchantUser.getGst())
                .storePinCode(merchantUser.getAddress().getPincode())
                .merchantDisplayId(merchantUser.getDisplayId())
                .merchantApiCredentialsDto(getByMerchantAPIKey(merchantUser))
                .build();
    }

    private String getMerchantToken(MerchantUser merchantUser) {
        Optional<List<MerchantSession>> optionalMerchantSessionList = merchantSessionBO.findByMerchantIdAndInvalid(merchantUser.getId().toString(), false);
        if (optionalMerchantSessionList.isPresent() && optionalMerchantSessionList.get().size() > 0) {
            if (StringUtils.hasText(optionalMerchantSessionList.get().get(0).getPassword())) {
                return merchantSessionBO.getToken(optionalMerchantSessionList.get().get(0));
            } else {
                return addMerchantSession(merchantUser);
            }
        } else {
            return addMerchantSession(merchantUser);
        }
    }

    private String getMerchantBasicAuthToken(MerchantUser merchantUser) {
        String merchantSecretKey = authUserBO.checkAndCreatePgApiKey(merchantUser.getDisplayId(), "merchant");
        String merchantClientId = merchantUser.getDisplayId();
        byte[] clientIdAndSecret = (merchantClientId + ":" + merchantSecretKey).getBytes(StandardCharsets.US_ASCII);
        String encodedClientIdAndSecret = Base64.getEncoder().encodeToString(clientIdAndSecret);
        return "Basic " + encodedClientIdAndSecret;
    }

    private String addMerchantSession(MerchantUser merchantUser) {
        List<String> authorities = Collections.singletonList("MERCHANT");
        String token = jwtTokenBO.generateToken(merchantUser.getId().toString(), "", authorities);
        merchantSessionBO.saveSession(String.valueOf(merchantUser.getId()), merchantUser.getMobile(), token);
        return token;
    }

    private MerchantApiCredentialsDto getByMerchantAPIKey(MerchantUser merchantUser) {
        PaymentConfigInfo paymentConfigInfo = paymentServiceBO.getPaymentConfig(merchantUser.getId().toString());
        if (null != paymentConfigInfo && null != paymentConfigInfo.getSecurityCredentials()) {
            merchantUser.setSecurityCredentials(paymentConfigInfo.getSecurityCredentials());
            merchantUserBO.save(merchantUser);
            return MerchantApiCredentialsDto.builder()
                    .token(getMerchantBasicAuthToken(merchantUser))
                    .ivKey(merchantUser.getSecurityCredentials().getIvKey())
                    .merchantResponseKey(paymentConfigInfo.getMerchantResponseKey())
                    .secretKey(merchantUser.getSecurityCredentials().getSecretKey())
                    .build();
        }
        return MerchantApiCredentialsDto.builder()
                .build();

    }
}
