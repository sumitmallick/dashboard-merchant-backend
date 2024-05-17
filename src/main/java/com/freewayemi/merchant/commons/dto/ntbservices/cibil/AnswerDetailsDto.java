package com.freewayemi.merchant.commons.dto.ntbservices.cibil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AnswerDetailsDto {
    private String questionKey;

    private List<String> answerKey;

    private String userInputAnswer;

    private String skipQuestion;

    private String resend;
}
