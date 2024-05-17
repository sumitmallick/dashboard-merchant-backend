package com.freewayemi.merchant.dto;

import lombok.Data;

@Data
public class PanDocumentRequest {
    private String pan;
    private String type;
}
