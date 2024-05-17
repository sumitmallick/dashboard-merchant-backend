package com.freewayemi.merchant.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GlobalSearchRequestDto {
    @NotNull(message = "Please provide searchText.")
    private  String searchText;
    @NotNull(message = "Please provide limit.")
    private  Integer limit;
    @NotNull(message = "Please provide offset.")
    private  Integer offset;
    private  String brandType;
    private  Boolean isPopular;

}

