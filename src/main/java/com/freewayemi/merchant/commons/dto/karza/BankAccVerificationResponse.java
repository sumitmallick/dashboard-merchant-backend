package com.freewayemi.merchant.commons.dto.karza;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BankAccVerificationResponse {

    private final Result result;
    private final String error;
    private final String requestId;
    private final String statusCode;
    Instant createdDate;

    @JsonCreator
    public BankAccVerificationResponse(@JsonProperty("result") Result result,
                                       @JsonProperty("error") String error,
                                       @JsonProperty("status-code") String statusCode,
                                       @JsonProperty("request_id") String requestId) {
        this.result = result;
        this.error = error;
        this.requestId = requestId;
        this.statusCode = statusCode;
        this.createdDate = Instant.now();
    }

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
