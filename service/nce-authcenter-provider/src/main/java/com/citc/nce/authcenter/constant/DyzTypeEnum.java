package com.citc.nce.authcenter.constant;

import org.apache.commons.lang3.StringUtils;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/20 17:14
 */
public enum DyzTypeEnum {

    /**
     * 0：缺省
     */
    DEFAULT(0, "缺省(硬核桃)"),

    /**
     * 1：5G应用平台
     */
    NORMAL(1, "5G应用平台"),

    /**
     * 2：管理平台
     */
    ADMINAUTH(2, "管理平台");

    int code;
    String name;
    DyzTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(int code) {
        for (DyzTypeEnum name:
                DyzTypeEnum.values()) {
            if (code == name.getCode()) {
                return name.getName();
            }
        }
        return "";
    }

    public static int getCodeByName(String name) {
        for (DyzTypeEnum code:
                DyzTypeEnum.values()) {
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
