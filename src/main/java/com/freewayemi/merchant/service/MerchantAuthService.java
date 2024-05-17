package com.freewayemi.merchant.service;

import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.commons.exception.FreewayCustomException;
import com.freewayemi.merchant.commons.exception.MerchantException;
import com.freewayemi.merchant.commons.type.MerchantResponseCode;
import com.freewayemi.merchant.dto.MerchantAuthDto;
import com.freewayemi.merchant.enums.MerchantAuthSource;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class MerchantAuthService {

    private final List<String> dashboardApiKeys;
    private final List<String> dashboardAuthKeys;
    private final Boolean isDashboardKeysValidationEnabled;
    private final MerchantUserBO merchantUserBO;

    @Autowired
    public MerchantAuthService(@Value("${merchant.dashboard.api.keys}") List<String> dashboardApiKeys,
                               @Value("${merchant.dashboard.auth.keys}") List<String> dashboardAuthKeys,
                               @Value("${merchant.dashboard.validation.enabled}") Boolean isDashboardKeysValidationEnabled,
                               MerchantUserBO merchantUserBO) {
        this.dashboardApiKeys = dashboardApiKeys;
        this.dashboardAuthKeys = dashboardAuthKeys;
        this.isDashboardKeysValidationEnabled = isDashboardKeysValidationEnabled;
        this.merchantUserBO = merchantUserBO;
    }

    public void doAuth(MerchantAuthDto merchantAuthDto) {
        if (isValidHeaderKeys(merchantAuthDto)) {
            return;
        }
        throw new FreewayCustomException(400, "Merchant-Authorization failed.");
    }

    public void doAuthHeaders(MerchantAuthDto merchantAuthDto){
        if(isValidateHeaderKeys(merchantAuthDto)){
            return;
        }
        throw new FreewayCustomException(400, "Merchant-Authorization failed");
    }

    public boolean isValidateHeaderKeys(MerchantAuthDto ntbAuthDto) {
        MerchantAuthSource source = ntbAuthDto.getSource();
        String headerApiKey = ntbAuthDto.getRequest().getHeader("X-API-KEY");
        String headerAuthKey = ntbAuthDto.getRequest().getHeader("X-AUTH-KEY");
        switch (source) {
            case INTERNAL:
                if (BooleanUtils.isTrue(isDashboardKeysValidationEnabled)) {
                    return validateKeys(dashboardApiKeys, dashboardAuthKeys, headerApiKey, headerAuthKey);
                }
        }
        return true;
    }

    public void authenticate(MerchantAuthDto merchantAuthDto) {
        if (merchantAuthDto.getSource() != null) {
            boolean authenticated = false;

            switch (merchantAuthDto.getSource()) {
                case INTERNAL:
                    authenticated = isValidHeaderKeys(merchantAuthDto);
                    break;
                case MERCHANT:
                    authenticated = isValidMerchantKey(merchantAuthDto);
                    break;
            }
            if (authenticated) {
                return;
            }
        }
        throw new MerchantException(MerchantResponseCode.UNAUTHORIZED);
    }

    private boolean isValidMerchantKey(MerchantAuthDto merchantAuthDto) {
        String headerApiKey = merchantAuthDto.getRequest().getHeader("X-API-KEY");
        if (StringUtils.hasText(headerApiKey)) {
            String merchantApiKey = merchantUserBO.getCachedMerchantApiKey(merchantAuthDto.getMerchantIdOrDisplayId());
            return headerApiKey.equals(merchantApiKey);
        }
        return false;
    }

    public boolean isValidHeaderKeys(MerchantAuthDto ntbAuthDto) {
        String headerApiKey = ntbAuthDto.getRequest().getHeader("X-API-KEY");
        String headerAuthKey = ntbAuthDto.getRequest().getHeader("X-AUTH-KEY");
        if (BooleanUtils.isTrue(isDashboardKeysValidationEnabled)) {
            return validateKeys(dashboardApiKeys, dashboardAuthKeys, headerApiKey, headerAuthKey);
        }
        return true;
    }
    private boolean validateKeys(List<String> apiKeys, List<String> authKeys, String apiKey, String authKey) {
        if (!CollectionUtils.isEmpty(apiKeys) && !CollectionUtils.isEmpty(authKeys)) {
            return StringUtils.hasText(apiKey) && apiKeys.contains(apiKey) &&
                    StringUtils.hasText(authKey) && authKeys.contains(authKey);
        } else if (!CollectionUtils.isEmpty(apiKeys)) {
            return StringUtils.hasText(apiKey) && apiKeys.contains(apiKey);
        } else if (!CollectionUtils.isEmpty(authKeys)) {
            return StringUtils.hasText(authKey) && authKeys.contains(authKey);
        }
        return true;
    }

}
