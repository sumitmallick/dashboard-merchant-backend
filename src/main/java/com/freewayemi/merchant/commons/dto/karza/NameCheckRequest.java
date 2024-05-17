package com.freewayemi.merchant.commons.dto.karza;

import lombok.Data;

@Data
public class NameCheckRequest {
    private final String name1;
    private final String name2;
    private final String type;
    private final String preset;

    public NameCheckRequest(String name1, String name2, String type, String preset) {
        this.name1 = name1;
        this.name2 = name2;
        this.type = type;
        this.preset = preset;
    }
}
