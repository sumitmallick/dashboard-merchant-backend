package com.freewayemi.merchant.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailRequest {
    private String subject;
    private String toEmailIds;
    private String ccEmailIds;
    private String attachment;
    private String textBody;
    private String htmlBody;
}
