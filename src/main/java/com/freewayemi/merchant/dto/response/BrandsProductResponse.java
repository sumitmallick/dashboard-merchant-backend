package com.freewayemi.merchant.dto.response;

import lombok.Data;

import java.util.List;


@Data
public class BrandsProductResponse {
    private String name;
    private List<Variant> variants;

    public BrandsProductResponse(String name, List<Variant> variants) {
        this.name = name;
        this.variants = variants;
    }
}
