package com.freewayemi.merchant.dto.sales;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WeeklyTransaction {

    private Long success;
    private Long attempt;
    private LocalDateTime date;
}
