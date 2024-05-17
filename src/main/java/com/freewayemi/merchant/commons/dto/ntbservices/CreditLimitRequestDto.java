package com.freewayemi.merchant.commons.dto.ntbservices;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreditLimitRequestDto {

    private String mobileNumber;

    private String pan;

    private String emailId;

    private String dob;

    private String provider;

    private String latitude;

    private String longitude;

    private String ip;

    private String prospectId;

    private String loanStatus;
}
