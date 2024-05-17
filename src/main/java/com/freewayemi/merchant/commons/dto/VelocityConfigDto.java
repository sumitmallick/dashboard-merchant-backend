package com.freewayemi.merchant.commons.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.freewayemi.merchant.commons.type.OfferType;
import com.freewayemi.merchant.entity.VelocityConfig;
import lombok.Data;

import java.time.Instant;

@Data
public class VelocityConfigDto {

    private Integer velocity;
    private Instant validFrom;
    private Instant validTo;
    private OfferType offerType;

    public VelocityConfigDto(VelocityConfig velocityConfig) {
        if (velocityConfig != null) {
            this.velocity = velocityConfig.getVelocity();
            this.validFrom = velocityConfig.getValidFrom();
            this.validTo = velocityConfig.getValidTo();
            this.offerType = velocityConfig.getOfferType();
        }
    }

    @JsonCreator
    public VelocityConfigDto(@JsonProperty("velocity") Integer velocity, @JsonProperty("validFrom") Instant validFrom,
                             @JsonProperty("validTo") Instant validTo, @JsonProperty("offerType") OfferType offerType) {
        this.velocity = velocity;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.offerType = offerType;
    }

    public static VelocityConfigDto fromVelocityConfig(VelocityConfig velocityConfig) {
        if (velocityConfig != null) {
            // Returning null if velocityConfig is invalid
            if ((velocityConfig.getValidFrom() != null && Instant.now().isBefore(velocityConfig.getValidFrom())) ||
                    (velocityConfig.getValidTo() != null && Instant.now().isAfter(velocityConfig.getValidTo()))) {
                return null;
            }
            VelocityConfigDto velocityConfigDto = new VelocityConfigDto(velocityConfig);
            // setting default values if not present
            if (velocityConfigDto.getOfferType() == null) {
                velocityConfigDto.setOfferType(OfferType.SUBVENTION_AND_ADDITIONAL_CASHBACK);
            }
            if (velocityConfigDto.getVelocity() == null) {
                velocityConfigDto.setVelocity(1);
            }
            return velocityConfigDto;
        }
        return null;
    }
}
