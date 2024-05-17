package com.freewayemi.merchant.bo.validator.conditions.seamlesspaymentlink;

import com.freewayemi.merchant.bo.BrandProductBO;
import com.freewayemi.merchant.commons.dto.BrandInfo;
import com.freewayemi.merchant.commons.dto.BrandRequest;
import com.freewayemi.merchant.commons.dto.PaymentTransactionRequest;
import com.freewayemi.merchant.commons.dto.PgTransactionRequest;
import com.freewayemi.merchant.commons.exception.FreewayCustomException;
import com.freewayemi.merchant.commons.exception.FreewayException;
import com.freewayemi.merchant.commons.paymentlinks.Conditions;
import com.freewayemi.merchant.commons.type.TransactionCode;
import com.freewayemi.merchant.commons.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Component
public class IsValidProductModelOfBrand implements Conditions {

    private static final Logger LOGGER = LoggerFactory.getLogger(IsValidProductModelOfBrand.class);

    private final BrandProductBO brandProductBO;

    @Autowired
    public IsValidProductModelOfBrand(BrandProductBO brandProductBO) {
        this.brandProductBO = brandProductBO;
    }

    @Override
    public void executeConditions(Object obj) throws FreewayException {
        PgTransactionRequest request = (PgTransactionRequest) obj;
        if (!CollectionUtils.isEmpty(request.getProducts())) {
            PaymentTransactionRequest.ProductInfo productInfo = request.getProducts().get(0);
            if (StringUtils.hasText(productInfo.getBrandId())) {
                BrandInfo brandInfo = brandProductBO.getBrandInfo(new BrandRequest(null, productInfo.getProductId(), productInfo.getModel(), productInfo.getBrandId(), null));
                if (Util.isNull(brandInfo)) {
                    LOGGER.error("For brand transaction of : {} model number : {} doesn't exist", productInfo.getBrandId(), productInfo.getModel());
                    throw new FreewayCustomException(TransactionCode.FAILED_167);
                }
            }
        }
    }

    @Override
    public void executeConditions(Object obj1, Object obj2, Object obj3) throws FreewayException {

    }
}
