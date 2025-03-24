package com.citc.nce.auth.constant;

/**
 * @author huangchong
 * @date 2022/7/14 17:29
 * @describe
 */
public enum MailActivatedEnum {

    UNACTIVATED(0, "unactivated"),
    ACTIVATED(1, "activated");

    private int type;

    private String title;

    MailActivatedEnum(int type, String title) {
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
        for (MailActivatedEnum s : MailActivatedEnum
                .values()) {
            if (s.getType() == index) {
                return s.getTitle();
            }
        }
        return "";
    }

}
