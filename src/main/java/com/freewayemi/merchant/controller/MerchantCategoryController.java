package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.MerchantCategoryBO;
import com.freewayemi.merchant.dto.response.MerchantCategoryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MerchantCategoryController {
    private final MerchantCategoryBO merchantCategoryBO;

    @Autowired
    public MerchantCategoryController(MerchantCategoryBO merchantCategoryBO) {
        this.merchantCategoryBO = merchantCategoryBO;
    }

    @GetMapping("/api/v1/categories")
    public List<MerchantCategoryResponse> getCategories() {
        return merchantCategoryBO.getAll();
    }

    @GetMapping("/api/v2/categories")
    public List<MerchantCategoryResponse> getCategoriesV2() {
        return merchantCategoryBO.getAllV2();
    }

    @PostMapping("/api/v1/categories")
    public void create(@RequestBody MerchantCategoryResponse request) {
        merchantCategoryBO.create(request.getCategory());
    }
}
