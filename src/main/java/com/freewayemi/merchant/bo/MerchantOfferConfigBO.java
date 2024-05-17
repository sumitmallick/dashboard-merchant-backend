package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.bo.NotificationService;
import com.freewayemi.merchant.commons.dto.offer.DynamicOffer;
import com.freewayemi.merchant.commons.dto.offer.DynamicOfferResponse;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.type.CardTypeEnum;
import com.freewayemi.merchant.entity.DynamicOfferTemplate;
import com.freewayemi.merchant.entity.MerchantOfferConfig;
import com.freewayemi.merchant.entity.MerchantUser;
import com.freewayemi.merchant.repository.MerchantOfferConfigRepository;
import com.freewayemi.merchant.utils.MerchantStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.freewayemi.merchant.commons.utils.paymentConstants.SALES_REFERRAL_CODE;

@Component
public class MerchantOfferConfigBO {
    private final MerchantOfferConfigRepository merchantOfferConfigRepository;
    private final DynamicOfferTemplateBO dynamicOfferTemplateBO;
    private final NotificationService notificationService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantOfferConfigBO.class);

    @Autowired
    public MerchantOfferConfigBO(
            MerchantOfferConfigRepository merchantOfferConfigRepository,
            DynamicOfferTemplateBO dynamicOfferTemplateBO,
            NotificationService notificationService) {
        this.merchantOfferConfigRepository = merchantOfferConfigRepository;
        this.dynamicOfferTemplateBO = dynamicOfferTemplateBO;
        this.notificationService = notificationService;
    }

    public DynamicOfferResponse getDynamicOfferResponse(MerchantUser merchantUser) {
        if (
                (MerchantStatus.approved.name().equals(merchantUser.getStatus()) ||
                        (MerchantStatus.profiled.name().equals(merchantUser.getStatus())
                                && (null != merchantUser.getReferredBy() &&
                                SALES_REFERRAL_CODE.contains(merchantUser.getReferredBy().toUpperCase()))
                        )
                )
                        && null != merchantUser.getDynamicOffers() && merchantUser.getDynamicOffers()) {
            MerchantOfferConfig config = merchantOfferConfigRepository.findByMerchantId(merchantUser.getId().toString())
                    .orElseGet(() -> global(merchantUser.getId().toString(), merchantUser.getDynamicOfferTemplate()));
            return DynamicOfferResponse.builder().type(config.getType()).lowCostEmi(config.getLowCostEmi())
                    .dynamicOffers(config.getOffers()).ccBaseRate(config.getCcBaseRate())
                    .enableConvenienceFee(config.getEnableConvenienceFee())
                    .dcBaseRate(config.getDcBaseRate()).build();
        }
        throw new FreewayException("merchant dynamic offers not enabled.");
    }

    public MerchantOfferConfig global(String merchantId, String template) {
        DynamicOfferTemplate dot =
                dynamicOfferTemplateBO.findByName(StringUtils.isEmpty(template) ? "global" : template);
        MerchantOfferConfig config = new MerchantOfferConfig();
        config.setMerchantId(merchantId);
        config.setDynamicOfferTemplateId(dot.getId().toString());
        config.setType(dot.getType());
        config.setOffers(dot.getOffers());
        config.setMargins(dot.getMargins());
        config.setCcBaseRate(dot.getCcBaseRate());
        config.setDcBaseRate(dot.getDcBaseRate());
        return merchantOfferConfigRepository.save(config);
    }

    public void save(MerchantOfferConfig config) {
        merchantOfferConfigRepository.save(config);
    }

    public MerchantOfferConfig get(String merchantId) {
        return merchantOfferConfigRepository.findByMerchantId(merchantId)
                .orElseThrow(() -> new FreewayException("config doesen't exist"));
    }

    public Map<String, String> getMerchantOffersTenuresText(MerchantUser user) {
        Map<String, String> map = new HashMap<>();
        try {
            if (null != user.getDynamicOffers() && user.getDynamicOffers()) {
                MerchantOfferConfig config = merchantOfferConfigRepository.findByMerchantId(user.getId().toString())
                        .orElseGet(() -> this.global(user.getId().toString(), user.getDynamicOfferTemplate()));
                DynamicOfferResponse dynamicOfferResponse =
                        DynamicOfferResponse.builder().type(config.getType()).lowCostEmi(config.getLowCostEmi())
                                .dynamicOffers(config.getOffers()).ccBaseRate(config.getCcBaseRate())
                                .dcBaseRate(config.getDcBaseRate()).build();
                List<DynamicOffer> dynamicOfferDebit = dynamicOfferResponse.getDynamicOffers().stream()
                        .filter(dynamicOffer -> CardTypeEnum.DEBIT.name().equals(dynamicOffer.getCardType()))
                        .collect(Collectors.toList());
                List<Integer> noCostEmiList = dynamicOfferDebit.stream()
                        .filter(dynamicOffer -> dynamicOffer.getSelected() != null && dynamicOffer.getSelected())
                        .map(DynamicOffer::getTenure).collect(Collectors.toList());
                if (noCostEmiList.size() == 0) {
                    return map;
                }
                map.put("No-Cost EMi", getOffersString(noCostEmiList));
                    if (dynamicOfferResponse.getLowCostEmi()) {
                    List<Integer> lowCostEmiList = dynamicOfferDebit.stream()
                            .filter(dynamicOffer -> dynamicOffer.getSelected() != null && !dynamicOffer.getSelected())
                            .map(DynamicOffer::getTenure).collect(Collectors.toList());
                    if (lowCostEmiList.size() != 0) {
                        map.put("Low-Cost EMi", getOffersString(lowCostEmiList));
                    }
                } else {
                    List<Integer> standardEmiList = dynamicOfferDebit.stream()
                            .filter(dynamicOffer -> dynamicOffer.getSelected() != null && !dynamicOffer.getSelected())
                            .map(DynamicOffer::getTenure).collect(Collectors.toList());
                    if (standardEmiList.size() != 0) {
                        map.put("Standard EMi", getOffersString(standardEmiList));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while fetching home controller.", e);
            notificationService
                    .sendException(String.format("MerchantAPP:: Home controller issue for %s", user.getDisplayId()));
        }
        return map;
    }


    private String getOffersString(List<Integer> tenures) {
        if (tenures.size() == 1) {
            return tenures.get(0) + " months";
        } else {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < tenures.size() - 2; i++) {
                s.append(tenures.get(i));
                s.append(", ");
            }
            s.append(tenures.get(tenures.size() - 2));
            s.append(" and ");
            s.append(tenures.get(tenures.size() - 1));
            s.append(" months");
            return s.toString();
        }
    }
}
