package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.dto.MerchantConfigDto;
import com.freewayemi.merchant.dto.ReSubmissionValue;
import com.freewayemi.merchant.entity.MerchantConfigs;
import com.freewayemi.merchant.entity.MerchantConfigsV2;
import com.freewayemi.merchant.repository.MerchantConfigsRepository;
import com.freewayemi.merchant.repository.MerchantConfigsV2Repository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class MerchantConfigBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantConfigBO.class);

    private final MerchantConfigsV2Repository merchantConfigsV2Repository;
    private final MerchantConfigsRepository merchantConfigsRepository;

    @Autowired
    public MerchantConfigBO(MerchantConfigsV2Repository merchantConfigsV2Repository,
                            MerchantConfigsRepository merchantConfigsRepository) {
        this.merchantConfigsV2Repository = merchantConfigsV2Repository;
        this.merchantConfigsRepository = merchantConfigsRepository;
    }

    public Map<String, String> findMerchantConfigByLabel(String label) {
        MerchantConfigsV2 merchantConfigsV2 = merchantConfigsV2Repository.findByLabel(label).orElse(null);
        Map<String, String> merchantConfigReasons = new HashMap<>();
        if (Objects.isNull(merchantConfigsV2)) {
            return merchantConfigReasons;
        }
        merchantConfigReasons = merchantConfigsV2.getValues()
                .stream()
                .collect(Collectors.toMap(ReSubmissionValue::getReason,
                        reSubmissionValue -> reSubmissionValue.getAction()));
        return merchantConfigReasons;
    }

    public MerchantConfigDto getMerchantConfigV1(String label) {
        if (StringUtils.isBlank(label)) {
            throw new FreewayException("Empty merchant config label");
        }
        MerchantConfigs merchantConfig = merchantConfigsRepository.findByLabel(label)
                .orElseThrow(() -> new FreewayException("Invalid merchant config label"));
        return MerchantConfigDto.builder()
                .label(merchantConfig.getLabel())
                .values(merchantConfig.getValues())
                .version(merchantConfig.getVersion())
                .build();
    }
}
