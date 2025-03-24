package com.citc.nce.misc.utils;


public class Base62Utils {
    private static final int SCALE = 62;

    private static final char[] BASE_62_ARRAY = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
            'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
            'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
            'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
    };

    private static final String BASE_62_CHARS = new String(BASE_62_ARRAY);

    /**
     * 把long类型转换成base62编码的字符串
     *
     * @param value
     * @return
     */
    public static String encode2Base62String(long value) {
        StringBuffer stringBuffer = new StringBuffer();
        while (value != 0) {
            stringBuffer.append(BASE_62_CHARS.charAt((int) (value % SCALE)));
            value = value / SCALE;
        }
        return stringBuffer.reverse().toString();
    }

    /**
     * 把base62类型转换成long编码的字符串
     *
     * @param value
     * @return
     */
    public static long decode2Base62Long(String value) {
        long result = 0;
        long coefficient = 1;
        String Base62 = new StringBuffer(value).reverse().toString();
        for (char base62 : Base62.toCharArray()) {
            result += BASE_62_CHARS.indexOf(base62) * coefficient;
            coefficient *= SCALE;
        }
        return result;
    }
}
