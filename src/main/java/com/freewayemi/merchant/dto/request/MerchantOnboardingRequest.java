package com.freewayemi.merchant.dto.request;

import com.freewayemi.merchant.commons.dto.DocumentInfo;
import com.freewayemi.merchant.dto.PanDocumentRequest;
import com.freewayemi.merchant.dto.VerificationDocuments;
import lombok.Data;

import java.util.List;

@Data
public class MerchantOnboardingRequest {
    private String gst;
    private List<PanDocumentRequest> panDetails;
    private List<VerificationDocuments> verficationDocuments;
    private String ownership;
    private String deviceToken;
}
