package com.citc.nce.utils;

import java.util.UUID;

/**
 * 生成uuid
 *
 * @author zhanghong
 * @date 2020/5/18 15:17
 */
public class UUIDUtil {

    /**
     * 获得一个UUID
     *
     * @return String UUID
     */
    public static String getuuid() {
        // 去掉"-"符号
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}


