package com.freewayemi.merchant.utils;

public class RedisKeyUtil {
    public static String getCardDataRedisKey() {
        return RedisKeysConstants.ELIGIBILITY_DATA;
    }

    public static String getMerchantApiKey(String id) {
        return RedisKeysConstants.MERCHANT_API_KEY_DATA + id;
    }

    public static String getMerchantOffersKey(String id) {
        return RedisKeysConstants.MERCHANT_OFFERS_DATA + id;
    }

    public static boolean isEncrypted(String key) {
        if (key != null) {
            return !key.startsWith(RedisKeysConstants.MERCHANT_OFFERS_DATA) &&
                    !key.startsWith(RedisKeysConstants.MERCHANT_API_KEY_DATA);
        }
        return true;
    }
}
