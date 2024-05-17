package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.service.RuleEngineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class DeviceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceController.class);

    private final RuleEngineService ruleEngineService;

    @Autowired
    public DeviceController(RuleEngineService ruleEngineService) {
        this.ruleEngineService = ruleEngineService;
    }

    @GetMapping("/deviceId")
    public ResponseEntity<?> getDeviceId() {
        return ResponseEntity.ok(ruleEngineService.getDeviceId());
    }
}
