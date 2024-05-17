package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.AdminAuthUserBO;
import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.dto.request.*;
import com.freewayemi.merchant.dto.response.StoreUserResponse;
import com.freewayemi.merchant.entity.AdminAuthUser;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.service.AuthCommonService;
import com.freewayemi.merchant.service.RuleEngineHelperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class AdminAuthUserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminAuthUserController.class);

    private final AdminAuthUserBO adminAuthUserBO;
    private final MerchantUserBO merchantUserBO;
    private final AuthCommonService authCommonService;
    private final RuleEngineHelperService ruleEngineHelperService;


    @Autowired
    public AdminAuthUserController(AdminAuthUserBO adminAuthUserBO, MerchantUserBO merchantUserBO,
                                   AuthCommonService authCommonService, RuleEngineHelperService ruleEngineHelperService) {
        this.adminAuthUserBO = adminAuthUserBO;
        this.merchantUserBO = merchantUserBO;
        this.authCommonService = authCommonService;
        this.ruleEngineHelperService = ruleEngineHelperService;
    }

    @PostMapping("/fosOwners/api/v1/notifications")
    public ResponseEntity<?> pushNotification(@RequestParam("type") String type,
                                              @RequestParam("merchantId") String merchantId,
                                              @RequestBody Map<String, String> request,
                                              HttpServletRequest httpServletRequest) {
        LOGGER.info("Received Request for sending notification to Admin Auth users with body {}", request);
        adminAuthUserBO.pushNotification(type, request, merchantId);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/api/v1/storeUsers/exist")
    public ResponseEntity<?> checkUserExists(@RequestBody CheckUserExist request,
                                             HttpServletRequest httpServletRequest) {
        LOGGER.info("Received Request for check if user already exists, request: {}", request);
        adminAuthUserBO.checkUserExists(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/storeUsers")
    public ResponseEntity<?> addStoreUser(@RequestParam(value = "mobile") String mobile,
                                          @RequestParam(value = "email", required = false) String email,
                                          @RequestParam(value = "name") String name,
                                          @RequestParam(value = "userType", required = false) String userType,
                                          @RequestParam(value = "partner", required = false) String partner,
                                          HttpServletRequest httpServletRequest) throws FreewayException {
        LOGGER.info("Add Store user request received with, mobile: {}, email: {}, name: {}, userType: {}", mobile, email, name, userType);
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        MerchantUser user = merchantUserBO.getUserById(merchantId);
        StoreUserResponse response = adminAuthUserBO.addStoreUser(name, mobile, email, userType, partner, user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/storeUsers")
    public List<StoreUserResponse> getUsers(@RequestParam(value = "partner", required = false) String partner,
                                            HttpServletRequest httpServletRequest) {
        LOGGER.info("Get store users received");
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        return adminAuthUserBO.getStoreUsers(merchantId);
    }

    @PostMapping("/api/v1/storeUsers/{sid}/validateOtp")
    public ResponseEntity<?> validateOtp(@PathVariable("sid") String storeUserId, @RequestBody OtpRequest request,
                                         HttpServletRequest httpServletRequest, @RequestHeader Map<String, String> headers) throws IOException {
        ruleEngineHelperService.saveMobileData(headers, storeUserId);
        LOGGER.info("Received Request for validate otp for storeUserId: {}, request: {}", storeUserId, request);
        adminAuthUserBO.validateOtp(storeUserId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/v1/storeUsers/{sid}/resendOtp")
    public ResponseEntity<?> resendOtp(@PathVariable("sid") String storeUserId, HttpServletRequest httpServletRequest) {
        LOGGER.info("Received Request for resend otp for storeUserId: {}", storeUserId);
        adminAuthUserBO.resendOtp(storeUserId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/v1/storeUsers/{sid}/update")
    public ResponseEntity<?> editStoreUser(@PathVariable("sid") String sid, @RequestBody StoreUserRequest request,
                                           HttpServletRequest httpServletRequest) {
        LOGGER.info("Edit store user received with sid: {}, request: {}", sid, request);
        StoreUserResponse response = adminAuthUserBO.editStoreUser(sid, request);
        return ResponseEntity.ok(response);

    }

    @PutMapping("/api/v1/storeUsers/update")
    public ResponseEntity<?> updateStoreUserDetails(@RequestBody StoreUserUpdateRequest request,
                                                    HttpServletRequest httpServletRequest) {
        LOGGER.info("Update payment Details received request: {}", request);
        Map<String, String> credentials = authCommonService.getMerchantId(httpServletRequest).getCredentials();
        String storeUserId = null != credentials ? credentials.get("storeUserId") : null;
        if (null != storeUserId) adminAuthUserBO.updateStoreUserDetails(storeUserId, request);
        else throw new FreewayException("Store User Does not exists");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/internal/api/v1/getAdminUsers/{id}")
    public ResponseEntity<?> getAdminUser(@PathVariable("id") String id,
                                          @RequestParam(value = "role", required = false) String role,
                                          @RequestParam(value = "status", required = false) List<String> statuses) {
        return ResponseEntity.ok(adminAuthUserBO.findByIdOrMerchantId(id, role, statuses));
    }

    @PostMapping("/internal/api/v1/saveAdminAuthUser")
    public ResponseEntity<?> saveAdminAuthUser(
            @RequestParam(value = "sendPasswordLink", required = false) Boolean sendPasswordLink,
            @RequestBody AdminAuthUser adminAuthUser) {
        return ResponseEntity.ok(adminAuthUserBO.saveAdminAuthUser(adminAuthUser, sendPasswordLink));
    }

    @PostMapping("/internal/api/v1/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest,
                                   @RequestHeader(value = "DeviceToken") String deviceToken,
                                   @RequestHeader(value = "Remote-Addr", required = false) String remoteAddress)
            throws IllegalAccessException {
        if (Objects.isNull(loginRequest) || StringUtils.isEmpty(loginRequest.getUserName()) ||
                StringUtils.isEmpty(loginRequest.getPassword())) {
            throw new FreewayException("Invalid userName or Password");
        }
        return ResponseEntity.ok(adminAuthUserBO.login(loginRequest, remoteAddress, deviceToken));
    }

    @PostMapping("/internal/api/v1/salesLogout")
    public ResponseEntity<?> logout(@RequestHeader(value = "DeviceToken") String deviceToken,
                                    HttpServletRequest httpServletRequest) {
        String userName = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        return ResponseEntity.ok(adminAuthUserBO.logout(userName));
    }
}
