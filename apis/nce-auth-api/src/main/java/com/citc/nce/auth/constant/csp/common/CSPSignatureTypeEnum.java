package com.citc.nce.auth.constant.csp.common;

import org.apache.commons.lang3.StringUtils;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/20 17:14
 */
public enum CSPSignatureTypeEnum {

    /**
     * -1:全部
     */
    ALL(-1, "全部"),

    /**
     * 0：缺省(硬核桃)，1：联通，2：移动，3：电信
     */
    VIDEO(0, "视频短信"),

    /**
     * 2：移动
     */
    OTHER(1, "其他");
    int code;
    String name;
    CSPSignatureTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(int code) {
        for (CSPSignatureTypeEnum name:
                CSPSignatureTypeEnum.values()) {
            if (code == name.getCode()) {
                return name.getName();
            }
        }
        return "";
    }

    public static int getCodeByName(String name) {
        for (CSPSignatureTypeEnum code:
                CSPSignatureTypeEnum.values()) {
            if (StringUtils.equals(name,code.getName())) {
                return code.code;
            }
        }
        return 0;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
