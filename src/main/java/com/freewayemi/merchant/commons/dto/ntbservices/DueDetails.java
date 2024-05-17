package com.freewayemi.merchant.commons.dto.ntbservices;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DueDetails {

    private String dueDate;

    private String dueAmount;

    private String overDueAmount;

    private String principal;

    private String interest;

}