package com.example.gmall.common.constant;

/**
 * @author Lucas (Weiye) Wang
 * @version 1.0.0
 * @date 22/2/2024 - 1:22 am
 * @Description
 */
public class RedisConst {

    public static final String SKU_DETAIL_CACHE = "sku:info:";

    public static final String SKUID_BITMAP = "skuids:bitmap";
    public static final String SKU_LOCK = "lock:sku:";
    public static final String CATEGORY_CACHE = "categories";
    public static final String LOGIN_USER = "login:user:";
    public static final String USER_ID_HEADER = "UserId";
    public static final String USER_TEMP_ID_HEADER = "userTempId";
    public static final String CART_INFO = "cart:info:";
    public static final Integer CART_ITEM_NUM_LIMIT = 200;
    public static final String REPEAT_TOKEN = "repeat:token:";
}
