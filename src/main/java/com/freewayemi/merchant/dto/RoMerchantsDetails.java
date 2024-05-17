package com.freewayemi.merchant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoMerchantsDetails {
    private String name;
    private Boolean leadExists;
    private Long leadCreated;
    private Long leadResubmissions;
    private Long leadOnboardings;
    private Long leadRejections;
    private Long leadOnBoardedMTD;
    private Long leadActivatedMTD;
    private Long leadcreatedMTD;
}
