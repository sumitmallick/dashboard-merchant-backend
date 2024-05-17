package com.freewayemi.merchant.commons.dto.payout;

import com.freewayemi.merchant.commons.type.PayeeType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayeeDetailsDto {

    private final PayeeType type;
    private final String payeeId;
    private final String mobile;
    private final String email;

}
