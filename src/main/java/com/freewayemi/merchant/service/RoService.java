package com.freewayemi.merchant.service;

import com.freewayemi.merchant.bo.AdminAuthUserBO;
import com.freewayemi.merchant.bo.MerchantUserBO;
import com.freewayemi.merchant.commons.bo.AuthUserBO;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.utils.DateUtil;
import com.freewayemi.merchant.commons.utils.ValidationUtil;
import com.freewayemi.merchant.dto.RoMerchantsDetails;
import com.freewayemi.merchant.dto.request.RoCreationRequest;
import com.freewayemi.merchant.dto.response.AdminAuthUserResponse;
import com.freewayemi.merchant.dto.response.BasicResponse;
import com.freewayemi.merchant.entity.AdminAuthUser;
import com.freewayemi.merchant.enums.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;

import static com.freewayemi.merchant.commons.utils.paymentConstants.PARTNER_SALES;

@Service
public class RoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoService.class);
    private final MerchantUserBO merchantUserBo;
    private final AdminAuthUserBO adminAuthUserBO;
    private final AuthUserBO authUserBO;

    @Autowired
    public RoService(MerchantUserBO merchantUserBo, AdminAuthUserBO adminAuthUserBO, AuthUserBO authUserBO) {
        this.merchantUserBo = merchantUserBo;
        this.adminAuthUserBO = adminAuthUserBO;
        this.authUserBO = authUserBO;
    }

    public RoMerchantsDetails dashBoard(String leadOwnerId) {
        Instant fromDate= DateUtil.startTimeCurrentDay(Instant.now());
        Instant endDate= DateUtil.endTimeOfTheDay(fromDate);
        AdminAuthUser adminAuthUser = merchantUserBo.findLeadOwner(leadOwnerId);
        Long leadsCreatedToday = merchantUserBo.getMerchantCreatedLeadsCountToday(leadOwnerId, fromDate , endDate);
        Long monthlyLeadsOnboarded =
                merchantUserBo.getMerchantLeadsOnboardedCountMonthly(leadOwnerId, fromDate, endDate, Boolean.TRUE);
        Long monthlyLeadsActivated =
                merchantUserBo.getMerchantLeadsActivatedCountMonthly(leadOwnerId, fromDate, endDate, Boolean.TRUE);
        Long monthlyLeadsCreated = merchantUserBo.getMerchantCreatedLeadsCountMonthly(leadOwnerId, fromDate , endDate);
        Long reSubmissionLeads = merchantUserBo.getStatusLeadsCount(leadOwnerId, "resubmission");
        Long rejectedLeads = merchantUserBo.getStatusLeadsCount(leadOwnerId, "rejected");
        Long onBoardingLeads = merchantUserBo.getOnBoardingLeadsCount(leadOwnerId,"approved");
        Long totalLeads = merchantUserBo.getAllLeadsCount(leadOwnerId);
        return RoMerchantsDetails.builder()
                .leadActivatedMTD(monthlyLeadsActivated)
                .leadOnBoardedMTD(monthlyLeadsOnboarded)
                .leadResubmissions(reSubmissionLeads)
                .leadOnboardings(onBoardingLeads)
                .leadRejections(rejectedLeads)
                .leadCreated(leadsCreatedToday)
                .leadcreatedMTD(monthlyLeadsCreated)
                .name(adminAuthUser.getName())
                .leadExists(totalLeads > 0)
                .build();
    }

    public BasicResponse create(RoCreationRequest roCreationRequest) {
        if (StringUtils.isEmpty(roCreationRequest.getEmail()) || StringUtils.isEmpty(roCreationRequest.getMobile()) ||
                StringUtils.isEmpty(roCreationRequest.getPartnerName()) ||
                StringUtils.isEmpty(roCreationRequest.getName())) {
            throw new FreewayException("Required fields are missing");
        }
        if (!ValidationUtil.validateMobileNumber(roCreationRequest.getMobile())) {
            throw new FreewayException("Mobile number is not valid");
        }
        AdminAuthUser adminAuthUser = adminAuthUserBO.findAdminAuthUserByMobileAndEmail(roCreationRequest.getMobile(),
                roCreationRequest.getEmail());
        if (Objects.nonNull(adminAuthUser)) {
            if (roCreationRequest.getMobile().equals(adminAuthUser.getMobile())) {
                throw new FreewayException("RO already Exists with this mobile");
            }
            if (roCreationRequest.getEmail().equals(adminAuthUser.getLogin())) {
                throw new FreewayException("RO already Exists with this email");
            }
        }
        AdminAuthUser user = adminAuthUserBO.findById(roCreationRequest.getSalesLead());
        if (Objects.isNull(user)) {
            throw new FreewayException("Sales Id is not present");
        }
        try {
            AdminAuthUser newAdminAuthUser = new AdminAuthUser();
            newAdminAuthUser.setUserType("SALES");
            newAdminAuthUser.setName(roCreationRequest.getName());
            newAdminAuthUser.setPartner(user.getPartner());
            newAdminAuthUser.setMobile(roCreationRequest.getMobile());
            newAdminAuthUser.setLogin(roCreationRequest.getEmail());
            newAdminAuthUser.setRole(PARTNER_SALES);
            newAdminAuthUser.setSalesLead(roCreationRequest.getSalesLead());
            newAdminAuthUser.setStatus(roCreationRequest.getStatus());
            adminAuthUserBO.createAdminUser(newAdminAuthUser);
            return BasicResponse.builder()
                    .statusMsg("Successfully RO created")
                    .status(Status.SUCCESS)
                    .statusCode(0)
                    .build();
        } catch (Exception ex) {
            LOGGER.info("Exception occured while creating ro" + ex);
        }
        return BasicResponse.builder().statusMsg("Failed to create RO").status(Status.FAILED).statusCode(20).build();
    }

    public List<AdminAuthUser> getRosList() {
        return adminAuthUserBO.getRoList();
    }

    public BasicResponse updateStatus(String mobile, String status) {
        AdminAuthUser adminAuthUser = adminAuthUserBO.findAdminAuthUserByMobile(mobile);
        if (Objects.isNull(adminAuthUser)) {
            throw new FreewayException("RO Id is not present");
        }
        try {
            adminAuthUser.setStatus(status);
            adminAuthUserBO.createAdminUser(adminAuthUser);
            return BasicResponse.builder()
                    .statusMsg("Successfully updated RO status")
                    .status(Status.SUCCESS)
                    .statusCode(0)
                    .build();
        } catch (Exception ex) {
            LOGGER.info("Exception occured while creating ro" + ex.getMessage());
        }
        return BasicResponse.builder()
                .statusMsg("Failed to update RO status")
                .status(Status.FAILED)
                .statusCode(20)
                .build();
    }

    public AdminAuthUserResponse getProfile(String leadOwnerId) {
        return authUserBO.getProfile(leadOwnerId);
    }
}
