package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.CatalogProductBO;
import com.freewayemi.merchant.dto.MerchantProductResponse;
import com.freewayemi.merchant.dto.request.CatalogProductRequest;
import com.freewayemi.merchant.dto.response.CatalogProduct;
import com.freewayemi.merchant.entity.MerchantProduct;
import com.freewayemi.merchant.service.AuthCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class CatalogProductController {
    private final CatalogProductBO catalogProductBO;
    private final AuthCommonService authCommonService;


    @Autowired
    public CatalogProductController(CatalogProductBO catalogProductBO, AuthCommonService authCommonService) {
        this.catalogProductBO = catalogProductBO;
        this.authCommonService = authCommonService;
    }

    @PostMapping("/api/v1/products")
    public CatalogProduct create(@RequestBody CatalogProductRequest request, HttpServletRequest httpServletRequest) {
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        return catalogProductBO.createProduct(merchantId, request);
    }

    @GetMapping("/api/v1/products")
    public List<CatalogProduct> get(HttpServletRequest httpServletRequest) {
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        return catalogProductBO.getProducts(merchantId);
    }

    @PutMapping("/api/v1/products/{pid}")
    public CatalogProduct update(@PathVariable("pid") String uuid,
                                 @RequestBody CatalogProductRequest request, HttpServletRequest httpServletRequest) {
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        return catalogProductBO.updateProduct(merchantId, uuid, request);
    }

    @DeleteMapping("/api/v1/products/{pid}")
    public CatalogProduct delete(@PathVariable("pid") String uuid, HttpServletRequest httpServletRequest) {
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        return catalogProductBO.deleteProduct(merchantId, uuid);
    }

    @PutMapping("/api/v1/products/{pid}/uploads")
    public CatalogProduct upload(@RequestParam("file") MultipartFile file,
                                 @PathVariable("pid") String uuid, HttpServletRequest httpServletRequest)
            throws Exception {
        String merchantId = authCommonService.getMerchantId(httpServletRequest).getMerchantIdOrDisplayId();
        return catalogProductBO.upload(merchantId, file, uuid);
    }

    @GetMapping("/api/v1/{mid}/productSearch")
    public List<MerchantProductResponse> getProductsBySearch(@PathVariable("mid") String merchantId,
                                                             @RequestParam("text") String text){
        return catalogProductBO.getProductsBySearch(merchantId, text);
    }
}
