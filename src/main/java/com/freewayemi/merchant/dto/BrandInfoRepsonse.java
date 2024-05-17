package com.freewayemi.merchant.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class BrandInfoRepsonse {

    public String id;

    public String name;

    public BrandInfoRepsonse(String id,String name){
        this.id=id;
        this.name=name;
    }
}
