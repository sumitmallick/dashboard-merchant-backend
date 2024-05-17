package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.dto.request.CreateEventRequest;
import com.freewayemi.merchant.dto.sales.PanDict;
import com.freewayemi.merchant.dto.sales.PincodeResp;
import com.freewayemi.merchant.entity.AdminAuthUser;
import com.freewayemi.merchant.repository.AdminAuthUserRepository;
import com.freewayemi.merchant.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
public class MerchantLocationBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantLocationBO.class);
    private final AdminAuthUserRepository adminAuthUserRepository;
    private final ReportService reportService;

    public MerchantLocationBO(AdminAuthUserRepository adminAuthUserRepository, ReportService reportService) {
        this.adminAuthUserRepository = adminAuthUserRepository;
        this.reportService = reportService;
    }

    public PanDict checkBdeLocation(PincodeResp bdeLocation, PanDict gstDict, String user, String merchantId) {
        AdminAuthUser bde1 = adminAuthUserRepository.findById(user).orElse(null);
        LOGGER.info("bde1: {}", bde1);
        if (Objects.nonNull(bde1)) {
            LOGGER.info("pincode  {} {} {} {} {}", bde1.getPincode(),bdeLocation.getPincCode(), bdeLocation.getCity(), gstDict.getCity(), gstDict.getPincode());
            if (StringUtils.hasText(bde1.getPincode()) &&
                    !bde1.getPincode().contains(gstDict.getPincode())) {
                reportService.createEvent(CreateEventRequest.builder()
                        .eventName("GST_FAILED")
                        .reason("Merchant doesn't belongs to your assigned territory")
                        .createdBy(user)
                        .createdByType("sales")
                        .merchantId(merchantId)
                        .build());
                return PanDict.builder()
                        .status(Boolean.FALSE)
                        .statusHeader("Merchant doesn't belongs to your assigned territory / area / PIN code.")
                        .statusMsg("Please do onboarding in your territory only, or ask your manger to enable new " +
                                "territory")
                        .build();
            }
            if (gstDict.getCity().equals(bde1.getCity())) {
                if (!gstDict.getPincode().equals(bdeLocation.getPincCode())) {
                    reportService.createEvent(CreateEventRequest.builder()
                            .eventName("GST_FAILED")
                            .reason("Remote onboarding not allowed for the same city")
                            .createdBy(user)
                            .createdByType("sales")
                            .merchantId(merchantId)
                            .build());
                    return PanDict.builder()
                            .status(Boolean.FALSE)
                            .statusHeader("Merchant doesn't belongs to your assigned territory / area / PIN code")
                            .statusMsg(
                                    "Remote onboarding not allowed for the same city.Please visit merchant store to " +
                                            "complete the onboarding process")
                            .build();
                }
            }
        }
        return null;
    }
}
