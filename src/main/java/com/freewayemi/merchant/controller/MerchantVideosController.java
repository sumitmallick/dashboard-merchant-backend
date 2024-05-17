package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.bo.MerchantHowToVideosBO;
import com.freewayemi.merchant.entity.MerchantHowToVideo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MerchantVideosController {
    private final MerchantHowToVideosBO howToVideosBO;

    @Autowired
    public MerchantVideosController(MerchantHowToVideosBO howToVideosBO) {
        this.howToVideosBO = howToVideosBO;
    }

    @GetMapping("/api/v1/videos")
    public List<MerchantHowToVideo> get(@RequestParam(value = "type", required = false) String type) {
        if (!StringUtils.isEmpty(type)) {
            return howToVideosBO.getVideosByType(type);
        }
        return howToVideosBO.getAll();
    }
}
