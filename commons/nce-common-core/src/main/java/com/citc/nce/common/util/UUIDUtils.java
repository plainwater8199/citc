package com.citc.nce.common.util;

import java.util.UUID;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/21 18:20
 */
public class UUIDUtils {

    public static String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
    }
}
