package com.citc.nce.misc.constant;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/2 16:59
 * @Version 1.0
 * @Description:
 */
public enum QualificationType {
    /*
     * 企业用户
     */
    BUSINESS_USER(10001),

    /*
     * 实名用户
     */
    REAL_NAME_USER(10002),

    /*
     * 入驻用户
     */
    CHECK_IN_USER(10003),

    /*
     *能力提供商
     */
    ABILITY_SUPPLIER(10004),

    /*
     *解决方案商
     */
    SOLUTION_PROVIDER(10005),

    /*
     *CSP用户
     */
    CSP_USER(10006);


    private Integer code;

    private QualificationType(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
