package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.dto.response.MerchantCategoryResponse;
import com.freewayemi.merchant.entity.MerchantCategory;
import com.freewayemi.merchant.repository.MerchantCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MerchantCategoryBO {
    private final MerchantCategoryRepository merchantCategoryRepository;

    @Autowired
    public MerchantCategoryBO(MerchantCategoryRepository merchantCategoryRepository) {
        this.merchantCategoryRepository = merchantCategoryRepository;
    }

    public List<MerchantCategoryResponse> getAll() {
        List<MerchantCategoryResponse> responses = new ArrayList<>();
        merchantCategoryRepository.findAll().forEach(mc -> {
            if (StringUtils.isEmpty(mc.getVersion())) {
                responses.add(new MerchantCategoryResponse(mc.getCategory(), mc.getSubCategories(), mc.getSubCategoryList(), mc.getOrder()));
            }
        });
        return responses;
    }

    public void create(String category) {
        MerchantCategory merchantCategory = new MerchantCategory();
        merchantCategory.setCategory(category);
        merchantCategoryRepository.save(merchantCategory);
    }

    public List<MerchantCategoryResponse> getAllV2() {
        List<MerchantCategoryResponse> responses = new ArrayList<>();
        Optional<List<MerchantCategory>> merchantCategoryListData = merchantCategoryRepository.findByVersionOrderByOrderAsc("2");
        if (merchantCategoryListData.isPresent()) {
            merchantCategoryListData.get().forEach(mc -> {
                if(!StringUtils.isEmpty(mc.getSubCategoryList())){
                    mc.getSubCategoryList().sort((sc1, sc2)-> sc1.getOrder().compareTo(sc2.getOrder()));
                }
                responses.add(new MerchantCategoryResponse(mc.getCategory(), mc.getSubCategories(), mc.getSubCategoryList(), mc.getOrder()));
            });
        }
        return responses;
    }
}
