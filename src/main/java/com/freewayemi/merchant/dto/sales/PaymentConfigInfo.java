package com.freewayemi.merchant.dto.sales;

import com.freewayemi.merchant.commons.entity.DownPaymentConfig;
import com.freewayemi.merchant.commons.entity.PaymentProviderInfo;
import com.freewayemi.merchant.commons.entity.SecurityCredentials;
import com.freewayemi.merchant.dto.sales.config.*;
import com.freewayemi.merchant.entity.HdfcDcEmiConfig;
import com.freewayemi.merchant.entity.IsgPgConfig;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
public class PaymentConfigInfo {

    private String merchantId;
    private String merchantResponseKey;
    private String webhookAuthToken;
    private Map<String, String> customWebhookHeaders;
    private Map<String, String> customParams;
    private IsgPgConfig isgPgConfig;
    private HdfcDcEmiConfig hdfcDcEmiConfig;
    private List<PaymentProviderInfo> pgSettings;
    private TpslEmiPgConfig tpslEmiPgConfig;
    private AxisPgConfig axisPgConfig;
    private KotakDcEmiConfig kotakDcEmiConfig;
    private IciciCardlessEmiConfig iciciCardlessEmiConfig;
    private SecurityCredentials securityCredentials;
    private HdfcPgConfig hdfcPgConfig;
    private FlexipayConfig flexipayConfig;
    private IdfcDcEmiConfig idfcDcEmiConfig;
    private PaymentParams paymentParams;
    private DownPaymentConfig downPaymentConfig;
    private LyraPgConfig lyraPgConfig;
    private EasebuzzPgConfig easebuzzPgConfig;
    private Instant lastModifiedDate;
}
