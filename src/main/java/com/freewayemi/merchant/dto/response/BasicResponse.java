package com.freewayemi.merchant.dto.response;

import com.freewayemi.merchant.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BasicResponse {
    private String statusMsg;
    private Status status;
    private Integer statusCode;
    private String header;
}
