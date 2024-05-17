package com.freewayemi.merchant.dto.sales;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.dto.Address;
import com.freewayemi.merchant.commons.dto.ntbservices.validators.Date;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SalesUserProfile {

    private String name;

    private String mobile;

    private String email;

    private Address address;

    private Instant dateOfJoining;

    private String managerName;

    private String managerMobile;

    private String managerEmailId;

    private String profilePhotoURL;

    private String userProfileUpdate;

    private String deviceToken;
}
