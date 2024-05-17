package com.freewayemi.merchant.commons.dto.ntbservices.cibil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
public class CibilUnderwritingResponseDto {
    private int initiateCibilStatus;

    private String finalBRMStatus;

    private String cibilVerificationStatus;

    private Boolean authenticationQuestionsVerified;

    private String eligibleAmount;

    private String requestId;

    private String cibilXml;

    private String challengeConfigGUID;

    private List<Questionnaire> questionnaires;
}
