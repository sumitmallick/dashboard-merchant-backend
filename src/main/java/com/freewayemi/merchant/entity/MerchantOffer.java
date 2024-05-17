package com.freewayemi.merchant.entity;

public class MerchantOffer extends Offer {
    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
