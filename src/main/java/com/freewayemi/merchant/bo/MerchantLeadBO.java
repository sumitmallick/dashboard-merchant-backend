package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.dto.OnBoardingStage;
import com.freewayemi.merchant.dto.request.MerchantLeadRequest;
import com.freewayemi.merchant.dto.request.MerchantLeadsRequest;
import com.freewayemi.merchant.entity.AdminAuthUser;
import com.freewayemi.merchant.entity.MerchantLead;
import com.freewayemi.merchant.enums.Status;
import com.freewayemi.merchant.pojos.gst.GstDetailsResponse;
import com.freewayemi.merchant.repository.AdminAuthUserRepository;
import com.freewayemi.merchant.repository.MerchantLeadRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.PublicKey;
import java.time.Instant;
import java.util.*;

import static com.freewayemi.merchant.commons.utils.paymentConstants.*;
import static com.freewayemi.merchant.enums.Status.PENDING;
import static com.freewayemi.merchant.enums.Status.SUCCESS;
import static com.freewayemi.merchant.enums.Status.*;


@Component
public class MerchantLeadBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantLeadBO.class);

    private final MerchantLeadRepository merchantLeadRepository;
    private final AdminAuthUserRepository adminAuthUserRepository;

    private final MerchantUserBO merchantUserBO;

    @Autowired
    public MerchantLeadBO(MerchantLeadRepository merchantLeadRepository,
                          AdminAuthUserRepository adminAuthUserRepository,
                          MerchantUserBO merchantUserBO) {
        this.merchantLeadRepository = merchantLeadRepository;
        this.adminAuthUserRepository = adminAuthUserRepository;
        this.merchantUserBO = merchantUserBO;
    }

//    private OnBoardingStage getStage(String step, Status status){
//        return OnBoardingStage.builder().name(step).status(status).createdDate(Instant.now()).lastModifiedDate(Instant.now()).build();
//    }

//    private List<OnBoardingStage> getOnBoardingStages() {
//        List<OnBoardingStage> onBoardingStages = new ArrayList<>();
//        onBoardingStages.add(getStage(ONBOARDING_STEP1, SUCCESS));
//        onBoardingStages.add(getStage(ONBOARDING_STEP2, PENDING));
//        onBoardingStages.add(getStage(ONBOARDING_STEP3, PENDING));
//        onBoardingStages.add(getStage(ONBOARDING_STEP4, PENDING));
//        onBoardingStages.add(getStage(ONBOARDING_STEP5, PENDING));
//        onBoardingStages.add(getStage(ONBOARDING_STEP6, PENDING));
//        onBoardingStages.add(getStage(ONBOARDING_STEP7, PENDING));
//        onBoardingStages.add(getStage(ONBOARDING_STEP8, PENDING));
//        return onBoardingStages;
//    }

//    public void createLead(MerchantLeadRequest merchantLeadRequest,
//                           GstDetailsResponse gstDetailsResponse, AdminAuthUser adminAuthUser) {
//        MerchantLead merchantLead = MerchantLead.builder()
//                .mobile(merchantLeadRequest.getMobile())
//                .status(Status.REGISTERED)
//                .email(merchantLeadRequest.getEmail())
//                .meCode(merchantLeadRequest.getMeCode())
//                .pinCode(merchantLeadRequest.getPinCode())
//                .gst(merchantLeadRequest.getGst())
//                .leadOwnerIds(Arrays.asList(adminAuthUser.getId().toString()))
//                .partner(adminAuthUser.getPartner())
//                .gstData(gstDetailsResponse.getGstData())
//                .stage("LEAD CREATED")
//                .displayId(merchantUserBO.createDisplayId())
//                .stages(getOnBoardingStages())
//                .build();
//        merchantLeadRepository.save(merchantLead);
//    }

//    public AdminAuthUser findLeadOwner(String leadOwnerId) {
//        return adminAuthUserRepository.findById(leadOwnerId)
//                .orElseThrow(() -> new FreewayException("User not exists."));
//    }
//
////    public MerchantLead getMerchantLeadByMobileAndPartner(String mobile, String partner) {
////        return merchantLeadRepository.findByMobileAndPartner(mobile, partner).orElse(null);
////    }
//
//    public long getMerchantCreatedLeadsCountToday(String leadOwnerId, int year, int month, int day) {
//        return merchantLeadRepository.findByLeadOwnerIdAndCreatedDate(leadOwnerId, year, month, day);
//    }
//
//    public long getMerchantLeadsOnboardedCountMonthly(String leadOwnerId, int year, int month, Boolean isOnboarded) {
//        return merchantLeadRepository.findByLeadOwnerIdAndApprovedDateAndOnBoardedTilMtd(leadOwnerId, year, month,
//                isOnboarded);
//    }
//
//    public long getMerchantLeadsActivatedCountMonthly(String leadOwnerId, int year, int month, Boolean isActivated) {
//        return merchantLeadRepository.findByLeadOwnerIdAndApprovedDateAndActivatedTilMtd(leadOwnerId, year, month,
//                isActivated);
//    }
//
//    public long getMerchantCreatedLeadsCountMonthly(String leadOwnerId, int year, int month) {
//        return merchantLeadRepository.findByLeadOwnerIdAndCreatedDateTillMtd(leadOwnerId, year, month);
//    }
//
//    public long getStatusLeadsCount(String leadOwnerId, String status) {
//        return merchantLeadRepository.findByLeadOwnerIdAndStatusLeadsCount(leadOwnerId, status);
//    }
//
//    public long getOnBoardingLeadsCount(String leadOwnerId, List<String> statuses) {
//        return merchantLeadRepository.findByLeadOwnerIdAndOnBoardingLeadsCount(leadOwnerId, statuses);
//    }
//
//    public long getAllLeadsCount(String leadOwnerId) {
//        return merchantLeadRepository.findAllLeadsByLeadOwnerId(leadOwnerId);
//    }
//
//    public List<MerchantLead> getMerchantLeads(MerchantLeadsRequest merchantLeadsRequest) {
//        LOGGER.info("MerchantLeadsRequest: {}", merchantLeadsRequest);
//        List<Status> statuses = new ArrayList<>();
//        Status status = null;
//        if (!Objects.equals(merchantLeadsRequest.getStatus(), "null")) {
//            status = Status.getStatusByValue(merchantLeadsRequest.getStatus());
//        }
//        if (Objects.nonNull(status)) {
//            if (RESUBMISSION.equals(status)) {
//                statuses = Collections.singletonList(RESUBMISSION);
//            }
//            if (REJECTED.equals(status)) {
//                statuses = Collections.singletonList(REJECTED);
//            }
//            if (APPROVED.equals(status)) {
//                statuses = Collections.singletonList(APPROVED);
//            }
//            if (ONBOARDING.equals(status)) {
//                statuses = Arrays.asList(RESUBMISSION, REJECTED, APPROVED);
//            }
//        }
//        LOGGER.info("status: {} {}", statuses, status);
//        if (!Objects.equals(merchantLeadsRequest.getName(), "null") && !StringUtils.isEmpty(merchantLeadsRequest.getName()) &&
//                Objects.nonNull(status)) {
//            if (ONBOARDING.equals(status)) {
//                return merchantLeadRepository.findByStatusAndBussinessNameForOnBoarding(
//                        merchantLeadsRequest.getLeadOwnerId(),
//                        statuses, merchantLeadsRequest.getName()).orElse(new ArrayList<>());
//            }
//            return merchantLeadRepository.findByStatusAndBussinessName(merchantLeadsRequest.getLeadOwnerId(),
//                    statuses, merchantLeadsRequest.getName()).orElse(new ArrayList<>());
//        }
//        LOGGER.info("merchantLeadsRequest and status : {} {}", merchantLeadsRequest, status);
//        if (Objects.nonNull(status)) {
//            if (ONBOARDING.equals(status)) {
//                return merchantLeadRepository.findByLeadOwnerIdsAndStatusOnBoarding(
//                        merchantLeadsRequest.getLeadOwnerId(),
//                        statuses).orElse(new ArrayList<>());
//            }
//            return merchantLeadRepository.findByLeadOwnerIdsAndStatus(merchantLeadsRequest.getLeadOwnerId(),
//                    statuses).orElse(new ArrayList<>());
//        }
//        if (!Objects.equals(merchantLeadsRequest.getName(), "null") && !StringUtils.isEmpty(merchantLeadsRequest.getName())) {
//            return merchantLeadRepository.findByLeadOwnerIdsAndBussinessName(merchantLeadsRequest.getLeadOwnerId(),
//                    merchantLeadsRequest.getName()).orElse(new ArrayList<>());
//        }
//        return merchantLeadRepository.findByLeadOwnerIds(merchantLeadsRequest.getLeadOwnerId())
//                .orElse(new ArrayList<>());
//    }
//
//    public MerchantLead getMerchantLeadByDisplayId(String displayId) {
//        return merchantLeadRepository.findByDisplayId(displayId)
//                .orElseThrow(() -> new FreewayException("Merchant lead is not exists"));
//    }
}
