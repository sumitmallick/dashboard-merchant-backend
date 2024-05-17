package com.freewayemi.merchant.dto.BankAccount;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BankAccountAuthResp
{
    private final String paymentRefId;
    private final Integer code;

    private final String status;
    private final String statusMessage;

    private final String accountNumber;
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$",message="ifsc validation")
    private final String ifsc;

    private final Boolean bankTxnStatus;
    private final String accountName;
    private final String bankResponse;

    @JsonCreator
    public BankAccountAuthResp(@JsonProperty("paymentRefId") String paymentRefId,
                               @JsonProperty("code") int code,
                               @JsonProperty("status") String status,
                               @JsonProperty("statusMessage") String statusMessage,
                               @JsonProperty("accountNumber") String accountNumber,
                               @JsonProperty("ifsc") String ifsc,
                               @JsonProperty("bankTxnStatus") Boolean bankTxnStatus,
                               @JsonProperty("accountName") String accountName,
                               @JsonProperty("bankResponse") String bankResponse) {
        this.paymentRefId = paymentRefId;
        this.code = code;
        this.status = status;
        this.statusMessage = statusMessage;
        this.accountNumber = accountNumber;
        this.ifsc = ifsc;
        this.bankTxnStatus = bankTxnStatus;
        this.accountName = accountName;
        this.bankResponse = bankResponse;
    }
}
