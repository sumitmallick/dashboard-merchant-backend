package com.freewayemi.merchant.dto.sales;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantTransactionVolumeResponse extends BaseResponse {
    private String monthYear;
    private Integer monthlyTransactionVolume;
    @Builder(builderMethodName = "baseResponseBuilder")
    public MerchantTransactionVolumeResponse(Integer code, String status, String message, String monthYear,
                                             Integer monthlyTransactionVolume) {
        super(code, status, message);
        this.monthYear = monthYear;
        this.monthlyTransactionVolume = monthlyTransactionVolume;
    }
}
