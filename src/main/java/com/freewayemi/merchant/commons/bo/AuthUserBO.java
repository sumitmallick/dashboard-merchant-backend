package com.freewayemi.merchant.commons.bo;

import com.freewayemi.merchant.commons.dto.AuthenticationResponse;
import com.freewayemi.merchant.commons.entity.AuthUser;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.repository.AuthUserRepository;
import com.freewayemi.merchant.commons.utils.EncryptionUtil;
import com.freewayemi.merchant.commons.utils.paymentConstants;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.dto.SecurityDetails;
import com.freewayemi.merchant.dto.request.LoginMerchantRequest;
import com.freewayemi.merchant.dto.response.AdminAuthUserResponse;
import com.freewayemi.merchant.dto.sales.BaseResponse;
import com.freewayemi.merchant.entity.AdminAuthUser;
import com.freewayemi.merchant.repository.AdminAuthUserRepository;
import com.freewayemi.merchant.service.AuthCommonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.freewayemi.merchant.commons.utils.paymentConstants.*;

@Component
public class AuthUserBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthUserBO.class);

    private final AuthUserRepository authUserRepository;
    private final NotificationService notificationService;
    private final Boolean isProduction;
    private final String paymentSecretKey;
    private final AdminAuthUserRepository adminAuthUserRepository;
    private final AuthCommonService authCommonService;

    @Autowired
    public AuthUserBO(AuthUserRepository authUserRepository, NotificationService notificationService,
                      @Value("${payment.deployment.env}") String env,
                      @Value("${payment.secret.key}") String paymentSecretKey,
                      AdminAuthUserRepository adminAuthUserRepository,
                      AuthCommonService authCommonService) {
        this.authUserRepository = authUserRepository;
        this.notificationService = notificationService;
        this.isProduction = paymentConstants.PRODENV.equals(env);
        this.paymentSecretKey = paymentSecretKey;
        this.adminAuthUserRepository = adminAuthUserRepository;
        this.authCommonService = authCommonService;
    }

    public void createPgApiKey(String merchantDisplayId, String apiKey, String type) {
        AuthUser mua = new AuthUser();
        mua.setUserType(type);
        mua.setUserId(merchantDisplayId);
        mua.setExpiry(-1);
        mua.setOtp(BCrypt.hashpw(apiKey, BCrypt.gensalt()));
        try {
            mua.setPassword(EncryptionUtil.encrypt(apiKey, paymentSecretKey, paymentConstants.payment_IV_KEY, "Hex", null));
        } catch (Exception e) {
            throw new FreewayException("Oops, something went wrong!");
        }
        authUserRepository.save(mua);
    }

    public String checkAndCreatePgApiKey(String brandDisplayId, String type) {
        String apiKey = null;
        List<AuthUser> authUserList = authUserRepository.findByUserId(brandDisplayId).orElse(null);
        if (!CollectionUtils.isEmpty(authUserList)) {
            try {
                apiKey = EncryptionUtil.decrypt(authUserList.get(0).getPassword(), paymentSecretKey,
                        paymentConstants.payment_IV_KEY, "Hex", null);
            } catch (Exception e) {
                throw new FreewayException("Oops, something went wrong!");
            }
        } else {
            apiKey = UUID.randomUUID().toString();
            createPgApiKey(brandDisplayId, apiKey, type);
        }
        return apiKey;
    }

    public List<AuthUser> getPgApiKey(String merchantDisplayId) {
        return authUserRepository.findByUserId(merchantDisplayId).orElse(null);
    }

    public void validatePg(String merchantId, String apiKey) {
        Optional<List<AuthUser>> optional = authUserRepository.findByUserId(merchantId);
        if (optional.isPresent()) {
            for (AuthUser authUser : optional.get()) {
                if (BCrypt.checkpw(apiKey, authUser.getOtp())) {
                    return;
                }
            }
        }
        throw new FreewayException("Invalid Credentials!");
    }

    //For Old Consumer Login and Signup
    public void createAuthUser(String mobile, String userId, String userType) {
        createAuthUser(mobile, userId, userType, false, false);
    }

    public void createAuthUser(String mobile, String userId, String userType, Boolean auto, Boolean retry) {
        createAuthUser(mobile, userId, userType, auto, retry, null, false);
    }

    public void createAuthUser(String mobile, String userId, String userType, Boolean auto, Boolean retry, String email,
                               Boolean isEmail) {
        AuthUser mua = authUserRepository.findTopByUserIdOrderByExpiryDesc(userId).orElse(new AuthUser());
        mua.setUserType(userType);
        mua.setUserId(userId);
        mua.setExpiry(System.currentTimeMillis() + 15 * 60 * 1000);
        String otp = Util.generateOtp(isProduction);
        mua.setOtp(BCrypt.hashpw(otp, BCrypt.gensalt()));
        authUserRepository.save(mua);
        if (auto) {
            notificationService.sendAutoOTP(otp, mobile, retry, userType);
        } else {
            notificationService.sendOTP(otp, mobile, retry, email, isEmail);
        }
    }

    public void validateClickWrap(String merchantId, String type, String otp) {
        Optional<AuthUser> optional = authUserRepository.findTopByUserIdAndUserTypeOrderByExpiryDesc(merchantId, type);
        if (optional.isPresent()) {
            AuthUser authUser = optional.get();
            if (!BCrypt.checkpw(otp, authUser.getOtp())) {
                throw new FreewayException("Invalid OTP");
            }
            if (System.currentTimeMillis() > authUser.getExpiry()) {
                throw new FreewayException("OTP Expired!");
            }
            return;
        }
        throw new FreewayException("OTP Not Found!");
    }

    public void validate(String merchantId, String otp) {
        LOGGER.info("Received validate OTP request for user id: {} with Otp: {}", merchantId, Util.truncateString(otp));
        Optional<AuthUser> optional = authUserRepository.findTopByUserIdOrderByExpiryDesc(merchantId);
        if (optional.isPresent()) {
            AuthUser authUser = optional.get();
            if (!BCrypt.checkpw(otp, authUser.getOtp())) {
                LOGGER.error("For user id: " + merchantId + " Invalid OTP");
                throw new FreewayException("Invalid OTP");
            }
            if (System.currentTimeMillis() > authUser.getExpiry()) {
                LOGGER.error("For user id: " + merchantId + " OTP Expired");
                throw new FreewayException("OTP Expired!");
            }
            if (!authUser.getUserId().equalsIgnoreCase(merchantId)) {
                LOGGER.error("For user id: " + merchantId + " does not match with db user id: " + authUser.getUserId());
                throw new FreewayException("Invalid OTP, Please try again");
            }
            return;
        }
        throw new FreewayException("OTP Not Found!");
    }

    public AuthenticationResponse getAuthorities(HttpServletRequest httpServletRequest) {
        SecurityDetails securityDetails = authCommonService.getMerchantId(httpServletRequest);
        Map<String, String> credentials = securityDetails.getCredentials();
        List<String> authorities = authCommonService.getMerchantId(httpServletRequest).getAuthorities();
        if (Objects.nonNull(credentials) &&
                !CollectionUtils.isEmpty(credentials)) {
            String storeUserId = credentials.get("storeUserId");
            return AuthenticationResponse.builder().authorities(authorities).roId(storeUserId).build();
        }
        return AuthenticationResponse.builder().build();
    }

    public AdminAuthUserResponse getProfile(String leadOwnerId) {
        AdminAuthUser adminAuthUser = adminAuthUserRepository.findById(leadOwnerId)
                .orElseThrow(() -> new FreewayException("User Not Found."));
        String leadName = "";
        if (Objects.nonNull(adminAuthUser.getSalesLead())) {
            AdminAuthUser leadOnwer = adminAuthUserRepository.findById(adminAuthUser.getSalesLead())
                    .orElseThrow(() -> new FreewayException("Owner Id is not present"));
            leadName = leadOnwer.getName();
        }
        return AdminAuthUserResponse.builder()
                .role(PARTNER_SALES.equals(adminAuthUser.getRole()) ? SALES : OWNER)
                .city(adminAuthUser.getCity())
                .designation(adminAuthUser.getDesignation())
                .DOB(adminAuthUser.getDOB())
                .partner(adminAuthUser.getPartner())
                .status(adminAuthUser.getStatus())
                .source(adminAuthUser.getSource())
                .name(adminAuthUser.getName())
                .userType(adminAuthUser.getUserType())
                .mobile(adminAuthUser.getMobile())
                .leadOwnerName(leadName)
                .email(adminAuthUser.getLogin())
                .build();
    }

    public BaseResponse sendOTP(LoginMerchantRequest loginRequest, Boolean resendOtp, String appVersion, String appType, HttpServletRequest httpServletRequest){
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        if(StringUtils.hasText(merchantId)) {
            createAuthUser(loginRequest.getMobile(), merchantId, AUTHUSER_MERCHANT,
                    !StringUtils.isEmpty(appVersion) && "android".equals(appType), null != resendOtp && resendOtp);
            return new BaseResponse(0, "SUCCESS", "OTP sent successfully");
        }
        throw new FreewayException("Something went wrong");
    }

}
