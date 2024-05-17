package com.freewayemi.merchant.dto.sales;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PennyDropResult {
    private Result result;
    private String requestId;
    private String statusCode;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {

        private final Boolean bankTxnStatus;
        private final String accountNumber;
        private final String ifsc;
        private final String accountName;
        private final String bankResponse;

        @JsonCreator
        public Result(@JsonProperty("bankTxnStatus") Boolean bankTxnStatus,
                      @JsonProperty("accountNumber") String accountNumber,
                      @JsonProperty("ifsc") String ifsc,
                      @JsonProperty("accountName") String accountName,
                      @JsonProperty("bankResponse") String bankResponse
        ) {
            this.bankTxnStatus = bankTxnStatus;
            this.accountNumber = accountNumber;
            this.ifsc = ifsc;
            this.accountName = accountName;
            this.bankResponse = bankResponse;
        }


    }
}
