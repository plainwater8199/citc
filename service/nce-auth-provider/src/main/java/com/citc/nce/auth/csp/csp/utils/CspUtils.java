package com.citc.nce.auth.csp.csp.utils;

import org.springframework.util.Assert;

/**
 * @author jiancheng
 */
public final class CspUtils {
    private CspUtils() {
    }

    public static String convertCspId(String customerId) {
        Assert.notNull(customerId, "customerId不能为空");
        if (customerId.length() != 15)
            throw new IllegalArgumentException("非法customerId");
        return customerId.substring(0, 10);
    }
}
