package com.freewayemi.merchant.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ConsentInfo {
    private String receiverEmail;
    private String receiverMobile;
    private String type;
    private String content;
}
