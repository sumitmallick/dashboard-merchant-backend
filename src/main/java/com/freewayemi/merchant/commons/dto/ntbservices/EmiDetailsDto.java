package com.freewayemi.merchant.commons.dto.ntbservices;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmiDetailsDto {

    private double emiAmount;

    private int tenure;

    private double cashbackAmount;

    private double downpaymentAmount;
}
