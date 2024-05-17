package com.freewayemi.merchant.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class PaymentProviderInfos {

    public String code;

    public String displayName;

    public String uuid;
    public List<SupportedCardTypeAndBankInfo> supportedCardTypeAndBankInfo;
}
