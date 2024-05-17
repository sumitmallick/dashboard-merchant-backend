package com.freewayemi.merchant.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminAuthUserResponse {
    private String name;
    private String role;
    private String partner;
    private String DOB;
    private String source;
    private String designation;
    private String city;
    private String userType;
    private String status;
    private String mobile;
    private String leadOwnerName;
    private String email;
}
