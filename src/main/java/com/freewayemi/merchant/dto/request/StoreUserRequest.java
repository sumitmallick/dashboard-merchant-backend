package com.freewayemi.merchant.dto.request;

import com.freewayemi.merchant.enums.StoreUserStatus;
import lombok.Data;

@Data
public class StoreUserRequest {
    private final String mobile;
    private final String name;
    private final String email;
    private final String DOB;
    private final StoreUserStatus status;
}
