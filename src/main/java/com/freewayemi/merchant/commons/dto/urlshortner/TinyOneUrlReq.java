package com.freewayemi.merchant.commons.dto.urlshortner;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TinyOneUrlReq {

    private final String url;
    private final String domain;

}
