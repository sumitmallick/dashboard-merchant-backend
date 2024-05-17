package com.freewayemi.merchant.dto.response;

import java.util.Arrays;

public class GeoCodeResponse {

    public plus_code plus_code;
    public String status ;
    public results[] results ;
    public GeoCodeResponse() {

    }
    public String getPincode() {
        if (null != results) {
            for (results result : results) {
                if (null != result.address_components) {
                    for (address_component row : result.address_components) {
                        if (null != row.types && Arrays.asList(row.types).contains("postal_code")) {
                            return row.long_name;
                        }
                    }
                }
            }
        }
        return "";
    }
}

class plus_code {
    public String compound_code;
    public String global_code;
}

class results{
    public String formatted_address ;
    public geometry geometry ;
    public String[] types;
    public address_component[] address_components;
    public String place_id;
    public plus_code plus_code;
}

class geometry{
    public bounds bounds;
    public String location_type ;
    public location location;
    public bounds viewport;
}

class bounds {

    public location northeast ;
    public location southwest ;
}

class location{
    public String lat ;
    public String lng ;
}

class address_component{
    public String long_name;
    public String short_name;
    public String[] types ;
}


