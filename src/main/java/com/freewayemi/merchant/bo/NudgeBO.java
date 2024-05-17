package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.utils.Util;
import com.freewayemi.merchant.commons.bo.AppContentBO;
import com.freewayemi.merchant.commons.entity.AppContents;
import com.freewayemi.merchant.commons.entity.Params;
import com.freewayemi.merchant.dto.response.NudgeResponse;
import com.freewayemi.merchant.entity.MerchantNudge;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.repository.MerchantNudgeRepository;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class NudgeBO {
    private final MerchantNudgeRepository merchantNudgeRepository;
    private final MerchantUserBO merchantUserBO;
    private final AppContentBO appContentBO;

    @Autowired
    public NudgeBO(MerchantNudgeRepository merchantNudgeRepository,
                   MerchantUserBO merchantUserBO, AppContentBO appContentBO) {
        this.merchantNudgeRepository = merchantNudgeRepository;
        this.merchantUserBO = merchantUserBO;
        this.appContentBO = appContentBO;
    }

    public List<NudgeResponse> getNudges(String merchantId, String type, String partner) {
        MerchantUser merchantUser = merchantUserBO.getUserById(merchantId);
        String userMobile = merchantUser.getMobile();
        MerchantUser partnerUser = null;
        List<String> partners = merchantUser.getPartners();
        if (Util.isNotNull(partner) && Util.isNotNull(partners)) {
            if (Util.isNotNull(partners) && partners.contains(partner)) {
                partnerUser = merchantUserBO.getUserByMobile(userMobile + "_" + partner);
                merchantUser = partnerUser;
            }
            else {
                throw new FreewayException(userMobile + " doesn't have any partner merchant with partner: " + partner);
            }
        }
        List<MerchantNudge> nudges = todaysNudgeV2(merchantId, type, merchantUser.getParams(), merchantUser);
        if (!CollectionUtils.isEmpty(nudges)) {
            return nudges.stream().map(this::convertNudgeToResponse).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    private List<MerchantNudge> todaysNudge(String merchantId, String type, Params params, MerchantUser merchantUser) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd")
                .withZone(ZoneOffset.systemDefault());
        String date = formatter.format(Instant.now());
        LocalDate localDate = LocalDate.parse(date);
        Instant from = localDate.atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant();
        Optional<MerchantNudge> mn = merchantNudgeRepository.todaysNudge(merchantId, type, true, from);
        if (mn.isPresent()) {
            return null;
        } else {
            List<MerchantNudge> merchantNudges = new ArrayList<>();
//            MerchantNudge nudge1 = getBrandNudge(merchantId, params, type);
//            if (null != nudge1) merchantNudges.add(nudge1);
            MerchantNudge nudge2 = getNudgeBaseCategoryAndBrandId(merchantUser, type);
            if (null != nudge2) merchantNudges.add(nudge2);
            return merchantNudges;
        }
    }

    private List<MerchantNudge> todaysNudgeV2(String merchantId, String type, Params params, MerchantUser merchantUser) {
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd")
                .withZone(ZoneOffset.systemDefault());
        String date = formatter.format(Instant.now());
        LocalDate localDate = LocalDate.parse(date);
        Instant from = localDate.atStartOfDay(ZoneId.of("Asia/Kolkata")).toInstant();
        List<MerchantNudge> merchantNudges = getMerchantNudges(merchantUser, type);
        List<MerchantNudge> showNudgesToUser = new ArrayList<>();
        for (MerchantNudge nudge : merchantNudges) {
            List<MerchantNudge> mn = merchantNudgeRepository.findNudgesShownToUser(merchantId, type, true, from, nudge.getNudgeId()).orElse(null);
            if (CollectionUtils.isEmpty(mn)) {
                showNudgesToUser.add(nudge);
                merchantNudgeRepository.save(nudge);
            } else if (null != nudge.getCount() && nudge.getCount() < mn.size()) {
                showNudgesToUser.add(nudge);
                merchantNudgeRepository.save(nudge);
            }
        }
        return showNudgesToUser;
    }

    private List<MerchantNudge> getMerchantNudges(MerchantUser merchantUser, String type) {
        List<MerchantNudge> merchantNudges = new ArrayList<>();
        MerchantNudge nudge = getNudgeBaseCategoryAndBrandId(merchantUser, type);
        //List<MerchantNudge> nudges = getNudgeBaseCategory(merchantUser, type);
        if (Util.isNotNull(nudge))
            merchantNudges.add(nudge);
        return merchantNudges;
    }

    public MerchantNudge getBrandNudge(String merchantId, Params params, String type) {
        if (null != params && StringUtils.hasText(params.getBrandId())) {
            AppContents au = appContentBO.findNudge(params.getBrandId());
            if (null != au && StringUtils.hasText(au.getIcon())) {
                MerchantNudge nudge = new MerchantNudge();
                nudge.setIcon(au.getIcon());
                nudge.setAction("Got it");
                nudge.setMerchantId(merchantId);
                nudge.setType(type);
                merchantNudgeRepository.save(nudge);
                return nudge;
            }
        }
        return null;
    }

    public MerchantNudge getNudgeBaseCategoryAndBrandId(MerchantUser merchantUser, String type) {
        if (StringUtils.hasText(merchantUser.getStatus()) && "approved".equalsIgnoreCase(merchantUser.getStatus()) && StringUtils.hasText(merchantUser.getType())
                && "offline".equalsIgnoreCase(merchantUser.getType())) {
            AppContents au = appContentBO.findNudgeBaseCategoryAndBrandId(merchantUser.getCategory(), merchantUser.getParams());
            if (null != au && StringUtils.hasText(au.getIcon())) {
                // TODO remove special check for merchant nudge
                if("Mobiles".equalsIgnoreCase(merchantUser.getCategory()) && CollectionUtils.isEmpty(au.getBrandIds())) {
                    return null;
                }
                MerchantNudge nudge = new MerchantNudge();
                nudge.setNudgeId(au.getId().toString());
                nudge.setIcon(au.getIcon());
                nudge.setAction("Got it");
                nudge.setMerchantId(merchantUser.getId().toString());
                nudge.setType(type);
                return nudge;
            }
        }
        return null;
    }


    private List<MerchantNudge> getNudgeBaseCategory(MerchantUser merchantUser, String type) {
        if (StringUtils.hasText(merchantUser.getStatus()) && "approved".equalsIgnoreCase(merchantUser.getStatus()) && StringUtils.hasText(merchantUser.getType())
                && "offline".equalsIgnoreCase(merchantUser.getType())) {
            List<MerchantNudge> nudges = new ArrayList<>();
            List<AppContents> appContentsList = appContentBO.findNudgeBaseCategory(merchantUser.getCategory());
            for (AppContents au : appContentsList) {
                if (null != au && StringUtils.hasText(au.getIcon())) {
                    MerchantNudge nudge = new MerchantNudge();
                    nudge.setNudgeId(au.getId().toString());
                    nudge.setIcon(au.getIcon());
                    nudge.setAction("Got it");
                    nudge.setMerchantId(merchantUser.getId().toString());
                    nudge.setType(type);
                    nudges.add(nudge);
                }
            }
            return nudges;
        }
        return null;
    }


    public void updateReadStatus(String nudgeId) {
        merchantNudgeRepository.findById(nudgeId).ifPresent(merchantNudge -> {
            merchantNudge.setIsRead(true);
            merchantNudge.setReadAt(Instant.now());
            merchantNudgeRepository.save(merchantNudge);
        });
    }

    private NudgeResponse convertNudgeToResponse(MerchantNudge nudge) {
        return NudgeResponse.builder()
                .nudgeId(nudge.getId().toString())
                .title(nudge.getTitle())
                .type(nudge.getType())
                .icon(nudge.getIcon())
                .text(nudge.getText())
                .action(nudge.getAction())
                .subText(nudge.getSubText())
                .build();
    }

    private MerchantNudge convertAppContentToNudge(String merchantId, AppContents contents, String type) {
        MerchantNudge nudge = new MerchantNudge();
        nudge.setIcon(contents.getIcon());
        nudge.setAction("Got it");
        nudge.setMerchantId(merchantId);
        nudge.setType(type);
        return nudge;
    }
}
