package com.citc.nce.auth.constant;

/**
 * @author huangchong
 * @date 2022/7/12 17:29
 * @describe
 */
public enum CheckUniqueTypeEnum {

    EMAIL(1, "mail"),
    NAME(2, "name"),
    PHONE(3, "phone"),
    MAIL(4, "mail");//用来校验是否激活

    private int type;

    private String title;

    CheckUniqueTypeEnum(int type, String title) {
        this.title = title;
        this.type = type;
    }

    public int getType() {
        return this.type;
    }

    public String getTitle() {
        return this.title;
    }

    public static String getTitle(int index) {
        for (CheckUniqueTypeEnum s : CheckUniqueTypeEnum
                .values()) {
            if (s.getType() == index) {
                return s.getTitle();
            }
        }
        return "";
    }

}
