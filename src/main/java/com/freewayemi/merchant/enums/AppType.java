package com.freewayemi.merchant.enums;
public enum AppType {
    ANDROID("android", "merchant_app_min_version_android"),
    IOS("ios", "merchant_app_min_version_ios");
    private String type;
    private String appVersionLabel;
    AppType(String type, String appVersionLabel) {
        this.type = type;
        this.appVersionLabel = appVersionLabel;
    }
    public static AppType getByType(String type){
        if (type == null) {
            return AppType.ANDROID;
        }
        type = type.trim();
        for (AppType appType:AppType.values()) {
            if(appType.type.equalsIgnoreCase(type)){
                return appType;
            }
        }
        return AppType.ANDROID;
    }
    public String getType() {
        return type;
    }

    public String getAppVersionLabel() {
        return appVersionLabel;
    }
}