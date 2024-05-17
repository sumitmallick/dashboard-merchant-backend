package com.freewayemi.merchant.commons.dto.karza;

import com.freewayemi.merchant.commons.utils.Util;
import lombok.Data;

@Data
public class TotalOCRRequest {
    private final String fileB64;
    private final Boolean maskAadhaar;
    private final Boolean hideAadhaar;
    private final Boolean conf;
    private final String docType;

    public TotalOCRRequest(String fileB64, Boolean maskAadhaar, Boolean hideAadhaar, Boolean conf, String docType) {
        this.fileB64 = fileB64;
        this.maskAadhaar = maskAadhaar;
        this.hideAadhaar = hideAadhaar;
        this.conf = conf;
        this.docType = docType;
    }

    @Override
    public String toString() {
        return "TotalOCRRequest{" +
                "fileB64='" + Util.getNoOfCharFromStringFromEnd(fileB64 , 10) +
                ", maskAadhaar=" + maskAadhaar +
                ", hideAadhaar=" + hideAadhaar +
                ", conf=" + conf +
                ", docType='" + docType +
                '}';
    }
}
