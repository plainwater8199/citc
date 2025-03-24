package com.citc.nce.utils;

import java.util.Random;

/**
 * @author ping chen
 */
public class AuthInfoUtils {
    private final static Random random = new Random();

    private AuthInfoUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void main(String[] args) {
        String randomString = generateRandomString("5GSMS");
        System.out.println(randomString);
    }

    /**
     * 随机生成字符串
     *
     * @param prefix 字符串前缀
     */
    public static String generateRandomString(String prefix) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        sb.append("-");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                sb.append(AuthInfoUtils.generateRandomCharacter());
            }
            if (i != 3) {
                sb.append("-");
            }
        }
        return sb.toString();
    }

    /**
     * 随机生成字符串
     */
    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(AuthInfoUtils.generateRandomCharacter());
        }
        return sb.toString();
    }

    public static char generateRandomCharacter() {
        boolean isDigit = random.nextBoolean();
        if (isDigit) {
            return (char) (random.nextInt(10) + '0');
        } else {
            return (char) (random.nextInt(26) + 'A');
        }
    }
}
