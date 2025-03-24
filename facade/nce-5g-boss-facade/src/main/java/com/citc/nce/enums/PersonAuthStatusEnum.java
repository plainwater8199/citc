package com.citc.nce.enums;

/**
 * 个人实名认证状态 0 未认证 1 认证审核中 2 认证不通过 3 认证通过'
 */
public enum PersonAuthStatusEnum {

    UN_AUTH(0, "UN_AUTH"),
    UNDER_REVIEW(1, "UNDER_REVIEW"),
    AUTH_FAILED(2, "AUTH_FAILED"),
    AUTH_OK(3, "AUTH_OK");

    private int type;

    private String title;

    PersonAuthStatusEnum(int type, String title) {
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
        for (PersonAuthStatusEnum s : PersonAuthStatusEnum
                .values()) {
            if (s.getType() == index) {
                return s.getTitle();
            }
        }
        return "";
    }
}
