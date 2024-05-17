package com.freewayemi.merchant.dto.response;
import lombok.Data;

import java.util.*;

@Data
public class ConsumerProfileConstants {
    public List<String> companyType;
    public List<String> employmentType;
    public List<String> educationType;
    public List<String> residentType;
    public List<String> loanStages;
}
