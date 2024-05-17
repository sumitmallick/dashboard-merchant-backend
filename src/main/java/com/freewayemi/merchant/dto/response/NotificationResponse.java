package com.freewayemi.merchant.dto.response;

import com.freewayemi.merchant.enums.Status;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponse {

    private Status status;

}
