package com.freewayemi.merchant.commons.dto.ntbservices.cibil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class Questionnaire {

    private String inputType;

    private String questionType;

    private String questionKey;

    private String questionText;

    private Boolean skip;

    private Boolean resend;

    private List<Answer> answers;
}
