package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.dto.MerchantProductResponse;
import com.freewayemi.merchant.dto.request.CatalogProductRequest;
import com.freewayemi.merchant.dto.response.CatalogProduct;
import com.freewayemi.merchant.entity.MerchantProduct;
import com.freewayemi.merchant.repository.CatalogProductRepository;
import com.freewayemi.merchant.utils.OffsetBasedPageRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CatalogProductBO {
    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogProductBO.class);
    private final CatalogProductRepository catalogProductRepository;
    private final FirebaseBO firebaseBO;

    @Autowired
    public CatalogProductBO(CatalogProductRepository catalogProductRepository,
                            FirebaseBO firebaseBO) {
        this.catalogProductRepository = catalogProductRepository;
        this.firebaseBO = firebaseBO;
    }

    public CatalogProduct createProduct(String merchantId,
                                        CatalogProductRequest request) {
        MerchantProduct merchantProduct = new MerchantProduct();
        merchantProduct.setMerchantId(merchantId);
        merchantProduct.setUuid(UUID.randomUUID().toString());
        merchantProduct.setProductId(request.getProductId());
        merchantProduct.setProductName(request.getProductName());
        merchantProduct.setProductCategory(request.getProductCategory());
        merchantProduct.setProductPrice(request.getProductPrice());
        merchantProduct.setProductImages(request.getProductImages());
        merchantProduct.setActive(true);
        merchantProduct.setGstIncluded(request.getGstIncluded());
        catalogProductRepository.save(merchantProduct);
        return getCatalogProduct(merchantProduct);
    }

    private CatalogProduct getCatalogProduct(MerchantProduct merchantProduct) {
        return CatalogProduct.builder()
                .productCategory(merchantProduct.getProductCategory())
                .productId(merchantProduct.getProductId())
                .productImages(merchantProduct.getProductImages())
                .productName(merchantProduct.getProductName())
                .productPrice(merchantProduct.getProductPrice())
                .uuid(merchantProduct.getUuid())
                .gstIncluded(merchantProduct.getGstIncluded()).build();
    }

    public List<CatalogProduct> getProducts(String merchantId) {
        return catalogProductRepository.findAllByMerchantIdAndActive(merchantId, true)
                .map(products -> products.stream().map(this::getCatalogProduct).collect(Collectors.toList()))
                .orElseGet(ArrayList::new);
    }

    public CatalogProduct updateProduct(String merchantId, String uuid,
                                        CatalogProductRequest request) {
        MerchantProduct merchantProduct = catalogProductRepository
                .findByMerchantIdAndUuidAndActive(merchantId, uuid, true)
                .orElseThrow(() -> new FreewayException("Product not found."));
        merchantProduct.setProductId(request.getProductId());
        merchantProduct.setProductName(request.getProductName());
        merchantProduct.setProductCategory(request.getProductCategory());
        merchantProduct.setProductPrice(request.getProductPrice());
        merchantProduct.setProductImages(request.getProductImages());
        merchantProduct.setGstIncluded(request.getGstIncluded());
        catalogProductRepository.save(merchantProduct);
        return getCatalogProduct(merchantProduct);
    }

    public CatalogProduct deleteProduct(String merchantId, String uuid) {
        MerchantProduct merchantProduct = catalogProductRepository
                .findByMerchantIdAndUuidAndActive(merchantId, uuid, true)
                .orElseThrow(() -> new FreewayException("Product not found."));
        merchantProduct.setActive(false);
        catalogProductRepository.save(merchantProduct);
        return getCatalogProduct(merchantProduct);
    }

    public CatalogProduct upload(String merchantId, MultipartFile file, String uuid) throws Exception {
        MerchantProduct merchantProduct = catalogProductRepository
                .findByMerchantIdAndUuidAndActive(merchantId, uuid, true)
                .orElseThrow(() -> new FreewayException("Product not found."));
        String path = "catalog/merchants/" + merchantId + "/products/" + uuid + "/images/" + file.getOriginalFilename();
        String url = firebaseBO.uploadImageByteArray(path, file.getContentType(), file.getBytes());
        merchantProduct.addImage(url);
        catalogProductRepository.save(merchantProduct);
        return getCatalogProduct(merchantProduct);
    }

    public List<MerchantProductResponse> getProductsBySearch(String merchantId, String text) {
        Pageable pageable = new OffsetBasedPageRequest(5, 0, new Sort(Sort.Direction.ASC, "productName"));
        List<MerchantProduct> merchantProducts = catalogProductRepository.findProductsByName(merchantId, text, pageable).orElse(new ArrayList<>());
        List<MerchantProductResponse> merchantProductResponses = new ArrayList<>();
        for(MerchantProduct merchantProduct: merchantProducts){
            MerchantProductResponse merchantProductResponse = MerchantProductResponse.builder()
                    .productCategory(merchantProduct.getProductCategory())
                    ._id(merchantProduct.getId().toString())
                    .productName(merchantProduct.getProductName())
                    .productPrice(merchantProduct.getProductPrice())
                    .active(merchantProduct.getActive())
                    .gstIncluded(merchantProduct.getGstIncluded())
                    .merchantId(merchantId)
                    .productImages(merchantProduct.getProductImages())
                    .productId(merchantProduct.getProductId())
                    .uuid(merchantProduct.getUuid())
                    .build();
            merchantProductResponses.add(merchantProductResponse);
        }
        return merchantProductResponses;
    }
}
