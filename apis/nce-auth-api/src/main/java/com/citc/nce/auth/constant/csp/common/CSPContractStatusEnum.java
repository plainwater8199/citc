package com.citc.nce.auth.constant.csp.common;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/16 10:02
 */
public enum CSPContractStatusEnum {

    /**
     * 缺省
     */

    STATUS_0_DEFAULT(0, "缺省"),


    /**
     * 11:新增审核不通过，
     */
    STATUS_10_INIT(10, "待分配服务代码"),

    /**
     * 11:新增审核不通过，
     */
    STATUS_11_CRATE_AND_AUDIT_NOT_PASS(11, "新增审核不通过"),

    /**
     * 12：变更审核不通过，
     */
    STATUS_12_UPDATE_AND_AUDIT_NOT_PASS(12, "变更审核不通过"),

    /**
     * 20：新增审核中，
     */
    STATUS_20_CRATE_AND_IS_AUDITING(20, "新增审核中"),

    /**
     * 22：待管理平台新增审核，
     */
    STATUS_22_WAIT_CRATE_AUDITING(22, "待管理平台新增审核"),

    /**
     * 23：待管理平台变更审核，
     */
    STATUS_23_WAIT_UPDATE_AUDITING(23, "待管理平台变更审核"),


    /**
     * 30：正常，
     */
    STATUS_30_ONLINE(30, "正常"),

    /**
     * 40：暂停
     */
    STATUS_40_PAUSE(40, "暂停"),

    /**
     * 60 新增待审核
     * 61 变更待审核
     * 62 注销待审核
     * 63 新增审核不通过
     * 64 变更审核不通过
     * 65 注销审核不通过
     * 66 正常
     * 67 已注销
     * 68 已过期
     */
    STATUS_60_NEW_WAIT_AUDIT(60, "新增待审核"),
    STATUS_61_EDIT_WAIT_AUDIT(61, "变更待审核"),
    STATUS_62_LOGOFF_WAIT_AUDIT(62, "注销待审核"),
    STATUS_63_NEW_AUDIT_NOT_PASS(63, "新增审核不通过"),
    STATUS_64_EDIT_AUDIT_NOT_PASS(64, "变更审核不通过"),
    STATUS_65_LOGOFF_AUDIT_NOT_PASS(65, "注销审核不通过"),
    STATUS_66_NORMAL(66, "正常"),
    STATUS_67_LOG_OFF(67, "已注销"),
    STATUS_68_EXPIRED(68, "已过期"),

    /**
     * 蜂动对接新增合同状态码
     * 25 移动-未提交
     * 26 移动-处理中
     * 31 移动-已注销
     * 69 电联-未提交
     * 70 电联-处理中
     */
    STATUS_25_NO_COMMIT(25, "未提交"),
    STATUS_26_PROCESSING(26, "处理中"),
    STATUS_31_LOG_OFF(31, "已注销"),
    STATUS_69_NO_COMMIT(69, "未提交"),
    STATUS_70_PROCESSING(70, "处理中");

    private final Integer code;
    private final String desc;

    CSPContractStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
