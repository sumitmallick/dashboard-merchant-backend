package com.freewayemi.merchant.commons.dto;

import lombok.Data;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

@Data
public class MerchantLocationResponse {
    private String shopName;
    private String emiOption;
    private String rating;
    private String mobile;
    private Address address;
    private Integer totalCount;
    private List<String> availableCategories;
    private List<BrandResponseDTO> availableBrands;
    private String distance;

    public void calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515; // miles
        dist = dist * 1.60934; // km
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.FLOOR);
        dist = new Double(df.format(dist));
        this.distance = dist + " km";
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}
