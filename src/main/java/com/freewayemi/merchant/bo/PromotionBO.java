package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.entity.AppContents;
import com.freewayemi.merchant.commons.entity.Params;
import com.freewayemi.merchant.commons.repository.AppContentsRepository;
import com.freewayemi.merchant.dto.response.PromotionResponse;
import com.freewayemi.merchant.entity.MerchantUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class PromotionBO {
    private final MerchantUserBO merchantUserBO;
    private final AppContentsRepository appContentsRepository;

    @Autowired
    public PromotionBO(MerchantUserBO merchantUserBO, AppContentsRepository appContentsRepository) {
        this.merchantUserBO = merchantUserBO;
        this.appContentsRepository = appContentsRepository;
    }
    public PromotionResponse getPromotion(String mid) {
        MerchantUser merchantUser = merchantUserBO.getUserById(mid);
        if (StringUtils.hasText(merchantUser.getStatus()) &&
                "approved".equalsIgnoreCase(merchantUser.getStatus()) &&
                StringUtils.hasText(merchantUser.getType()) &&
                "offline".equalsIgnoreCase(merchantUser.getType())) {
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("yyyy-MM-dd")
                    .withZone(ZoneOffset.systemDefault());
            Params params = merchantUser.getParams();
            String date = formatter.format(Instant.now());
            LocalDate localDate = LocalDate.parse(date);
            Instant from = localDate.atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant();
            List<AppContents> contents = appContentsRepository.findByCategoryAndActiveAndContentTypeAndExpiry(
                    merchantUser.getCategory(), true, "offer", from).orElse(null);
            if (null != contents && contents.size() > 0) {
                List<String> brandIds = CollectionUtils.isEmpty(params.getBrandIds()) ?
                        StringUtils.hasText(params.getBrandId()) ? Collections.singletonList(params.getBrandId()) :
                                new ArrayList<>() : params.getBrandIds();
                for (AppContents appContents : contents) {
                    if (CollectionUtils.isEmpty(brandIds) || !CollectionUtils.isEmpty(appContents.getBrandIds())) {
                        Optional<AppContents> optional = contents.stream().filter(content ->
                                !CollectionUtils.isEmpty(content.getBrandIds()) &&
                                        content.getBrandIds().containsAll(brandIds)).findFirst();
                        if (optional.isPresent()){
                            return PromotionResponse.builder()
                                    .status("success")
                                    .icon(optional.get().getIcon())
                                    .build();
                        }
                    }
                }
            }
        }
        return PromotionResponse.builder()
                .status("failed")
                .statusMsg("offers are not available")
                .build();
    }
}
