package com.citc.nce.authcenter.utils;

import cn.hutool.core.util.RandomUtil;

public class AuthUtils {

    private AuthUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 生成随机ID
     *
     * @param length 长度
     * @return id
     */
    public static String randomID(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(RandomUtil.randomInt(10));
        }
        return sb.toString();
    }


    public static void main(String[] args) {
        for (int i = 0; i < 21; i++) {
            System.out.println(randomID(10));
        }
    }
}
