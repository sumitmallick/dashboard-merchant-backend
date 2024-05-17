package com.freewayemi.merchant.commons.dto.ntb;

import lombok.Data;

import java.util.regex.Pattern;

@Data
public class CreditScoreFactor {
    private Integer closedAccounts;
    private Integer onTimePayments;
    private Integer totalPayments;
    private Integer activeAccounts;
    private Integer creditAgeYear;
    private Integer creditAgeMonth;
    private Integer inquiriesInLastSixMonths;

    public void calculatePaymentHistory(String combinedPaymentHistory) {
        Integer onTimePayment = 0, totalPayment = 0;
        int count = 0;
        for (String paymentHistory : combinedPaymentHistory.split("\\|")) {
            if (count++ < 12 && paymentHistory.contains("/")) {
                String dpd = paymentHistory.split("/")[0];
                if (dpd.contains(",")) {
                    String delayInDays = dpd.split(",")[1];
                    if (Pattern.matches("[0-9]*", delayInDays)) {
                        onTimePayment = "000".equalsIgnoreCase(delayInDays) ? onTimePayment + 1 : onTimePayment;
                        totalPayment += 1;
                    }
                }
            }
        }
        this.onTimePayments = null != this.onTimePayments ? this.onTimePayments + onTimePayment : onTimePayment;
        this.totalPayments = null != this.totalPayments ? this.totalPayments + totalPayment : totalPayment;
    }
}
