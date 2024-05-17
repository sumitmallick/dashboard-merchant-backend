package com.freewayemi.merchant.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class AddMerchantRequest {
    private Double latitude;
    private Double longitude;
    private String gst;
    private String brandId;
    private List<String> brandIds;

    @NotEmpty(message = "Please provide mobile number.")
    private final String mobile;

    @NotEmpty(message = "Please provide email.")
    @Email(message = "Please provide valid email.")
    private final String email;

    @NotEmpty(message = "Please provide first name.")
    private final String firstName;

    @NotEmpty(message = "Please provide last name.")
    private final String lastName;
}
