package com.freewayemi.merchant.dto;

import com.freewayemi.merchant.entity.AccountDetails;
import com.freewayemi.merchant.pojos.gst.GSTData;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GstDetailsInfo {
    private String address;
    private String type;
    private String gst;
    private String phoneNumber;
    private Boolean status;
    private String statusCode;
    private List<String> names;
    private String accountName;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String pincode;
    private String statusHeader;
    private String statusMsg;
    private String businessName;

}