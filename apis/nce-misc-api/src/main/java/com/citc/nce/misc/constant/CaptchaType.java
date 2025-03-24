package com.citc.nce.misc.constant;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/13 19:33
 * @Version 1.0
 * @Description:
 */
public enum CaptchaType {
    /*
     * 邮件
     */
    EMAIL(1, "EMAIL"),

    /*
     * 多因子短信
     */
    DYZ_SMS(2, "DYZ_SMS"),

    /*
     * 图片
     */
    IMAGE(3, "IMAGE"),

    /*
     * 普通短信
     */
    SMS(4, "SMS");

    private Integer num;
    private String code;

    private CaptchaType(Integer num, String code) {
        this.num = num;
        this.code = code;
    }

    public Integer getNum() {
        return num;
    }

    public String getCode() {
        return code;
    }
}
