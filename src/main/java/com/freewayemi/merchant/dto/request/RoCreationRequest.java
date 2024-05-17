package com.freewayemi.merchant.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoCreationRequest {
    private String email;
    private String mobile;
    private String name;
    private String partnerName;
    private String salesLead;
    private String status;
}
