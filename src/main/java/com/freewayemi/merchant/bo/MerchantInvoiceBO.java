package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.bo.S3UploadService;
import com.freewayemi.merchant.dto.response.MerchantMonthlyInvoiceDTO;
import com.freewayemi.merchant.entity.MerchantMonthlyInvoices;
import com.freewayemi.merchant.repository.MerchantMonthlyInvoicesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Component
public class MerchantInvoiceBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(MerchantInvoiceBO.class);

    private final MerchantMonthlyInvoicesRepository merchantMonthlyInvoicesRepository;
    private final S3UploadService s3UploadService;

    @Autowired
    public MerchantInvoiceBO(MerchantMonthlyInvoicesRepository merchantMonthlyInvoicesRepository, S3UploadService s3UploadService) {
        this.merchantMonthlyInvoicesRepository = merchantMonthlyInvoicesRepository;
        this.s3UploadService = s3UploadService;
    }

    public List<MerchantMonthlyInvoiceDTO> getMerchantMonthlyInvoice(String merchant) {
        LOGGER.error("Request received for merchant monthly invoices with merchant :{} ", merchant);
        List<MerchantMonthlyInvoiceDTO> merchantMonthlyInvoiceDTOList = new ArrayList<>();
        try {
            List<MerchantMonthlyInvoices> merchantMonthlyInvoicesList = merchantMonthlyInvoicesRepository.findByMerchantId(merchant);
            if (!CollectionUtils.isEmpty(merchantMonthlyInvoicesList)) {
                for (MerchantMonthlyInvoices merchantMonthlyInvoices : merchantMonthlyInvoicesList) {
                    merchantMonthlyInvoiceDTOList.add(new MerchantMonthlyInvoiceDTO(
                            merchantMonthlyInvoices.getMonth(),
                            merchantMonthlyInvoices.getYear(),
                            merchantMonthlyInvoices.getKey(),
                            merchantMonthlyInvoices.getMerchantId(),
                            merchantMonthlyInvoices.getFilename(),
                            s3UploadService.getPreSignedURL(merchantMonthlyInvoices.getKey()).get(0)
                    ));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred: ", e);
        }
        return merchantMonthlyInvoiceDTOList;
    }
}