package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CommercialPojo {
    private String title;
    private String heading;
    private String body;
    private Boolean bestSeller;
    private List<CommercialDetails> details;
}
