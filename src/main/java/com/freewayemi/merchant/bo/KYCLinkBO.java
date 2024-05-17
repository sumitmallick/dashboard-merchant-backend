package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.entity.KYCLink;
import com.freewayemi.merchant.repository.KYCLinkRepository;
import com.freewayemi.merchant.utils.OffsetBasedPageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class KYCLinkBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(KYCLinkBO.class);

    private final KYCLinkRepository kycLinkRepository;

    @Autowired
    public KYCLinkBO(KYCLinkRepository kycLinkRepository) {
        this.kycLinkRepository = kycLinkRepository;
    }

    public List<KYCLink> getKycLinks(String merchantId) {
        Pageable pageable = new OffsetBasedPageRequest(10000, 0, new Sort(Sort.Direction.DESC, "createdDate"));
        return kycLinkRepository.findByMerchantId(merchantId, pageable).orElse(new ArrayList<>());
    }

    public void saveKycLink(KYCLink kycLink){
        kycLinkRepository.save(kycLink);
    }
}
