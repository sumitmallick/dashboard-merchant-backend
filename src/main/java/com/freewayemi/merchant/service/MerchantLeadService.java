package com.freewayemi.merchant.service;

import com.freewayemi.merchant.bo.AdminAuthUserBO;
import com.freewayemi.merchant.bo.CreateParentMerchantRequest;
import com.freewayemi.merchant.bo.MerchantLeadBO;
import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.commons.bo.NotificationService;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.utils.paymentConstants;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.commons.utils.ValidationUtil;
import com.freewayemi.merchant.dto.request.CreateMerchantRequest;
import com.freewayemi.merchant.dto.request.MerchantLeadRequest;
import com.freewayemi.merchant.dto.request.MerchantLeadsRequest;
import com.freewayemi.merchant.dto.response.BasicResponse;
import com.freewayemi.merchant.entity.AdminAuthUser;
import com.freewayemi.merchant.entity.MerchantLead;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.enums.ResponseCode;
import com.freewayemi.merchant.pojos.gst.GstDetailsRequest;
import com.freewayemi.merchant.pojos.gst.GstDetailsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

import static com.freewayemi.merchant.commons.utils.paymentConstants.AUTHUSER_MERCHANT;

import static com.freewayemi.merchant.commons.utils.paymentConstants.AUTHUSER_MERCHANT;
import static com.freewayemi.merchant.commons.utils.paymentConstants.PARTNER_SALES;

@Service
public class MerchantLeadService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantLeadService.class);
//
//    private final MerchantLeadBO merchantLeadBO;
//    private final MerchantUserBO merchantUserBO;
//    private final DigitalIdentityService digitalIdentityService;
//    private final AdminAuthUserBO adminAuthUserBO;
//    private final NotificationService notificationService;
//
//    @Autowired
//    public MerchantLeadService(MerchantLeadBO merchantLeadBO, MerchantUserBO merchantUserBO,
//                               DigitalIdentityService digitalIdentityService, NotificationService notificationService,
//                               AdminAuthUserBO adminAuthUserBO) {
//        this.merchantLeadBO = merchantLeadBO;
//        this.merchantUserBO = merchantUserBO;
//        this.digitalIdentityService = digitalIdentityService;
//        this.notificationService = notificationService;
//        this.adminAuthUserBO = adminAuthUserBO;
//    }
//
//    private void leadRequestValidation(MerchantLeadRequest merchantLeadRequest) {
//        if (Objects.isNull(merchantLeadRequest)) {
//            throw new FreewayException("Request should not be null");
//        }
//        if (StringUtils.isEmpty(merchantLeadRequest.getMobile())) {
//            throw new FreewayException("Mobile Number should not be empty");
//        }
//        if (StringUtils.isEmpty(merchantLeadRequest.getEmail())) {
//            throw new FreewayException("Email should not be empty");
//        }
//        if (StringUtils.isEmpty(merchantLeadRequest.getGst())) {
//            throw new FreewayException("GST should not be empty");
//        }
//        if (StringUtils.isEmpty(merchantLeadRequest.getMeCode())) {
//            throw new FreewayException("ME Code should not be empty");
//        }
//        if (StringUtils.isEmpty(merchantLeadRequest.getPinCode())) {
//            throw new FreewayException("Pincode should not be empty");
//        }
//        if (!ValidationUtil.validateMobileNumber(merchantLeadRequest.getMobile())) {
//            throw new FreewayException("Invalid Mobile Number");
//        }
//    }
//
//    public BasicResponse createLead(String leadOwnerId, MerchantLeadRequest merchantLeadRequest) {
//        leadRequestValidation(merchantLeadRequest);
//        LOGGER.info("Request recieved to createLead method: {}", merchantLeadRequest);
//        String mobileNumber = merchantLeadRequest.getMobile();
//        String gst = merchantLeadRequest.getGst();
//        MerchantUser merchantUser = merchantUserBO.getUserByMobile(mobileNumber);
//        AdminAuthUser adminAuthUser = adminAuthUserBO.findById(leadOwnerId);
//        if (Objects.isNull(adminAuthUser)) {
//            throw new FreewayException("leadOwnerId is not exists");
//        }
//        MerchantLead merchantLead = merchantLeadBO.getMerchantLeadByMobileAndPartner(merchantLeadRequest.getMobile(),
//                adminAuthUser.getPartner());
//        LOGGER.info("merchantUser : {} and merchantLead : {}", merchantUser, merchantLead);
//        if (Objects.nonNull(merchantUser)) {
//            if (Objects.nonNull(merchantUser.getParentMerchant())) {
//                merchantUser = merchantUserBO.getUserById(merchantUser.getParentMerchant());
//            }
//            if (!gst.equals(merchantUser.getGst())) {
//                return BasicResponse.builder()
//                        .message(ResponseCode.MERCHANT_EXISTS_WITH_DIFFERENT_GST.getStatusMsg())
//                        .status(ResponseCode.MERCHANT_EXISTS_WITH_DIFFERENT_GST.getStatus())
//                        .statusCode(ResponseCode.MERCHANT_EXISTS_WITH_DIFFERENT_GST.getCode())
//                        .build();
//            } else {
//                notificationService.sendLeadCreationNotification("9502803691", "varaprasad");
//                return BasicResponse.builder()
//                        .message(ResponseCode.MERCHANT_EXISTS.getStatusMsg())
//                        .status(ResponseCode.MERCHANT_EXISTS.getStatus())
//                        .statusCode(ResponseCode.MERCHANT_EXISTS.getCode())
//                        .build();
//            }
//        }
//        if (Objects.nonNull(merchantLead)) {
//            return BasicResponse.builder()
//                    .message(ResponseCode.LEAD_ALREADY_CREATED.getStatusMsg())
//                    .status(ResponseCode.LEAD_ALREADY_CREATED.getStatus())
//                    .statusCode(ResponseCode.LEAD_ALREADY_CREATED.getCode())
//                    .build();
//        }
//        GstDetailsResponse gstDetailsResponse =
//                digitalIdentityService.getGstDetails(GstDetailsRequest.builder().gst(gst).build());
//        LOGGER.info("gstDetailsResponse : {}", gstDetailsResponse);
//        if (Objects.nonNull(gstDetailsResponse) && gstDetailsResponse.getCode() == 0) {
////            merchantLeadBO.createLead(merchantLeadRequest, gstDetailsResponse, adminAuthUser);
//
//            MerchantUser parentMerchantUser = merchantUserBO.createParentUser(merchantLeadRequest, paymentConstants.ONLINE, adminAuthUser);
//            AdminAuthUser newAdminAuthUser = new AdminAuthUser();
//            if (Util.isNotNull(adminAuthUser.getPartner())) {
//                newAdminAuthUser.setPartner(adminAuthUser.getPartner());
//            }
//            newAdminAuthUser.setMobile(merchantLeadRequest.getMobile());
//            newAdminAuthUser.setLogin(merchantLeadRequest.getEmail());
//            newAdminAuthUser.setMerchantId(parentMerchantUser.getId().toString());
//            newAdminAuthUser.setRole(AUTHUSER_MERCHANT);
//            newAdminAuthUser.setStatus(paymentConstants.STATUS_ACTIVE);
//            adminAuthUserBO.createAdminUser(newAdminAuthUser);
//            MerchantUser childMerchantUser = merchantUserBO.createChildMerchantUser(merchantLeadRequest, parentMerchantUser, paymentConstants.ONLINE, adminAuthUser);
//            merchantUserBO.updateParentMerchantUser(parentMerchantUser, adminAuthUser, childMerchantUser);
//
//
//            notificationService.sendLeadCreationNotification("9502803691", "varaprasad");
//            return BasicResponse.builder()
//                    .message(ResponseCode.LEAD_CREATED.getStatusMsg())
//                    .status(ResponseCode.LEAD_CREATED.getStatus())
//                    .statusCode(ResponseCode.LEAD_CREATED.getCode())
//                    .header("Congratulations!")
//                    .build();
//        } else {
//            return BasicResponse.builder()
//                    .message(ResponseCode.GST_VALIDAION_FAILED.getStatusMsg())
//                    .status(ResponseCode.GST_VALIDAION_FAILED.getStatus())
//                    .statusCode(ResponseCode.GST_VALIDAION_FAILED.getCode())
//                    .build();
//        }
//    }
//
//    public List<MerchantLead> getMerchantLeads(MerchantLeadsRequest merchantLeadsRequest){
//        return merchantLeadBO.getMerchantLeads(merchantLeadsRequest);
//    }
//    public MerchantLead getMerchantLeadByDisplayId(String displayId){
//        return merchantLeadBO.getMerchantLeadByDisplayId(displayId);
//    }
}
