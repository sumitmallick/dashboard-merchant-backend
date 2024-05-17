package com.freewayemi.merchant.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {
    @RequestMapping("/ping")
    public String ping() {
        return "Merchant:Success";
    }
}
