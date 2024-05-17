package com.freewayemi.merchant.dto.request;

import com.freewayemi.merchant.entity.AccountDetails;
import lombok.Data;

@Data
public class StoreUserUpdateRequest {
    private final Boolean consent;
    private final AccountDetails accountDetails;
}
