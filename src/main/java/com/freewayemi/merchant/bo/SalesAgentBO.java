package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.bo.S3UploadService;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.controller.AdminAuthUserController;
import com.freewayemi.merchant.dao.MerchantDAO;
import com.freewayemi.merchant.dto.request.Account;
import com.freewayemi.merchant.dto.request.CreateMerchantRequest;
import com.freewayemi.merchant.dto.response.NotificationResponse;
import com.freewayemi.merchant.dto.response.SalesAgentResponse;
import com.freewayemi.merchant.dto.sales.SalesUserProfile;
import com.freewayemi.merchant.entity.AdminAuthUser;
import com.freewayemi.merchant.entity.Notification;
import com.freewayemi.merchant.entity.SalesAgent;
import com.freewayemi.merchant.entity.SalesAgentWinner;
import com.freewayemi.merchant.enums.Status;
import com.freewayemi.merchant.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@Component
public class SalesAgentBO {
    private final SalesAgentRepository salesAgentRepository;
    private final SalesAgentWinnerRepository salesAgentWinnerRepository;

    private final NotificationRepository notificationRepository;

    private final AdminAuthUserRepository adminAuthUserRepository;
    private final S3UploadService s3UploadService;

    private final MerchantDAO merchantDAO;

    private Instant monthStarting = Instant.now();
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminAuthUserController.class);

    @Autowired
    public SalesAgentBO(SalesAgentRepository salesAgentRepository,
                        SalesAgentWinnerRepository salesAgentWinnerRepository,
                        NotificationRepository notificationRepository,
                        AdminAuthUserRepository adminAuthUserRepository, S3UploadService s3UploadService,
                        MerchantUserRepository merchantUserRepository,
                        MerchantDAO merchantDAO) {
        this.salesAgentRepository = salesAgentRepository;
        this.salesAgentWinnerRepository = salesAgentWinnerRepository;
        this.notificationRepository = notificationRepository;
        this.adminAuthUserRepository = adminAuthUserRepository;
        this.s3UploadService = s3UploadService;
        this.merchantDAO = merchantDAO;
    }

    public SalesAgent getOrCreateUser(CreateMerchantRequest request) {
        Optional<SalesAgent> optional = salesAgentRepository.findByMobile(request.getMobile());
        if (optional.isPresent()) {
            return optional.get();
        } else {
            SalesAgent salesAgent = new SalesAgent();
            salesAgent.setFirstName(request.getFirstName());
            salesAgent.setLastName(request.getLastName());
            salesAgent.setEmail(request.getEmail().toLowerCase());
            salesAgent.setMobile(request.getMobile());
            salesAgentRepository.save(salesAgent);
            return salesAgent;
        }
    }

    public SalesAgent getByMobile(String mobile) {
        Optional<SalesAgent> optional = salesAgentRepository.findByMobile(mobile);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new FreewayException("Something went wrong!", "sales", mobile);
    }

    public SalesAgentResponse getResponse(String agent) {
        Optional<SalesAgent> optional = salesAgentRepository.findById(agent);
        if (optional.isPresent()) {
            SalesAgent salesAgent = optional.get();
            return SalesAgentResponse.builder()
                    .firstName(salesAgent.getFirstName())
                    .lastName(salesAgent.getLastName())
                    .email(salesAgent.getEmail())
                    .mobile(salesAgent.getMobile())
                    .account(null == salesAgent.getAccount() ? null
                            : new Account(salesAgent.getAccount().getIfsc(), getMaskNumber(salesAgent.getAccount()),
                                    salesAgent.getFirstName()))
                    .build();
        }
        throw new FreewayException("SalesAgent not found!");
    }

    public SalesAgentResponse updateProfile(String agent, Account account) {
        Optional<SalesAgent> optional = salesAgentRepository.findById(agent);
        if (optional.isPresent()) {
            SalesAgent salesAgent = optional.get();
            salesAgent.setAccount(account);
            salesAgentRepository.save(salesAgent);
        }
        return getResponse(agent);
    }

    private String getMaskNumber(Account account) {
        return null == account ? "NA" : null == account.getNumber() || account.getNumber().length() <= 4 ? "NA"
                : account.getNumber().substring(0, 1) + "XXXX" +
                        account.getNumber().substring(account.getNumber().length() - 3);
    }

    public String checkForRewards(String agent) {
        Optional<SalesAgent> optional = salesAgentRepository.findById(agent);
        if (optional.isPresent()) {
            SalesAgent salesAgent = optional.get();
            Optional<SalesAgentWinner> optional1 =
                    salesAgentWinnerRepository.findByEmailAndActive(salesAgent.getEmail(), true);
            if (optional1.isPresent()) {
                return "success";
            }
        }
        return "failed";
    }

    public SalesUserProfile getSalesUserProfile(String leadOwnerId) {
        AdminAuthUser adminAuthUser = adminAuthUserRepository.findById(leadOwnerId).orElse(null);
        SalesUserProfile salesUserProfile = SalesUserProfile.builder().build();
        if (Objects.nonNull(adminAuthUser)) {
            if (Objects.nonNull(adminAuthUser.getReporter())) {
                AdminAuthUser reporterAuthUser =
                        adminAuthUserRepository.findById(adminAuthUser.getReporter()).orElse(null);
                if (Objects.nonNull(reporterAuthUser)) {
                    salesUserProfile.setManagerName(reporterAuthUser.getName());
                    salesUserProfile.setManagerMobile(reporterAuthUser.getMobile());
                    salesUserProfile.setManagerEmailId(reporterAuthUser.getLogin());
                }
            }
            salesUserProfile.setName(adminAuthUser.getName());
            salesUserProfile.setMobile(adminAuthUser.getMobile());
            salesUserProfile.setEmail(adminAuthUser.getLogin());
            salesUserProfile.setAddress(adminAuthUser.getAddress());
            salesUserProfile.setDateOfJoining(adminAuthUser.getCreatedDate());
            salesUserProfile.setUserProfileUpdate(adminAuthUser.getUserProfileUpdate());
            salesUserProfile.setDeviceToken(adminAuthUser.getDeviceToken());
            if (!CollectionUtils.isEmpty(adminAuthUser.getDocuments())) {

                salesUserProfile.setProfilePhotoURL(s3UploadService.getPreSignedURL(
                                adminAuthUser.getDocuments().get(adminAuthUser.getDocuments().size() - 1).getKey())
                        .get(0));
            }else{
                salesUserProfile.setProfilePhotoURL("");
            }
            return salesUserProfile;
        }
        throw new FreewayException("Admin_auth user agent not found");

    }

    public NotificationResponse updateNotification(String notificationId) {
        try {
            Optional<Notification> notif = notificationRepository.findById(notificationId);
            if (notif.isPresent()) {
                Notification notification = notif.get();
                notification.setReadStatus(Boolean.TRUE);
                notificationRepository.save(notification);
            }
            return NotificationResponse.builder().status(Status.SUCCESS).build();
        } catch (Exception e) {
            LOGGER.info("Exception occured while updating readStatus, Exception: {}".format(String.valueOf(e)));
            return NotificationResponse.builder().status(Status.FAILED).build();
        }
    }

}
