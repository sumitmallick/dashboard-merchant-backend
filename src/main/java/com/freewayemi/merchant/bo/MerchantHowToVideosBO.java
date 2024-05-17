package com.freewayemi.merchant.bo;

import com.freewayemi.merchant.entity.MerchantHowToVideo;
import com.freewayemi.merchant.repository.MerchantHowToVideosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MerchantHowToVideosBO {
    private final MerchantHowToVideosRepository merchantHowToVideosRepository;

    @Autowired
    public MerchantHowToVideosBO(MerchantHowToVideosRepository merchantHowToVideosRepository) {
        this.merchantHowToVideosRepository = merchantHowToVideosRepository;
    }

    public List<MerchantHowToVideo> getAll() {
        return merchantHowToVideosRepository.findAll();
    }
    public List<MerchantHowToVideo> getVideosByType(String type) {
        return merchantHowToVideosRepository.findByType(type).orElse(null);
    }
}
