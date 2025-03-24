package com.citc.nce.common.util;

import java.nio.charset.StandardCharsets;

/**
 * @author bydud
 * @since 2024/5/16 15:57
 */

public class MyBase64Util {

    public static String encodeToString(String raw) {
        if (!org.springframework.util.StringUtils.hasLength(raw)) {
            return raw;
        }
        return org.springframework.util.Base64Utils.encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }
    public static String decode(String raw) {
        if (!org.springframework.util.StringUtils.hasLength(raw)) {
            return raw;
        }
        return org.springframework.util.Base64Utils.encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }
}
