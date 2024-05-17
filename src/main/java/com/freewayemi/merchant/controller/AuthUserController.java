package com.freewayemi.merchant.controller;

import com.freewayemi.merchant.commons.bo.AuthUserBO;
import com.freewayemi.merchant.commons.dto.AuthenticationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
public class AuthUserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthUserController.class);

    private final AuthUserBO authUserBO;

    @Autowired
    public AuthUserController(AuthUserBO authUserBO) {
        this.authUserBO = authUserBO;
    }

    @PostMapping("/internal/api/v1/getAuthorities")
    public AuthenticationResponse getAuthorities(HttpServletRequest httpServletRequest) {
        LOGGER.info("Request to get authorities");
        return authUserBO.getAuthorities(httpServletRequest);
    }
}
