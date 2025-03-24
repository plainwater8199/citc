package com.citc.nce.auth.constant.csp.common;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 移动使用10到50区间状态码，电信和联通使用61到71区间状态码
 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/16 10:02
 */
@Getter
public enum CSPChatbotStatusEnum {

    /**
     * 缺省
     */
    STATUS_0_DEFAULT(0, "缺省"),
    /**
     * 新增审核不通过
     */
    STATUS_11_CRATE_AND_AUDIT_NOT_PASS(11, "新增审核不通过"),

    /**
     * 变更审核不通过
     */
    STATUS_12_UPDATE_AND_AUDIT_NOT_PASS(12, "变更审核不通过"),

    /**
     * 管理平台新增审核中
     */
    STATUS_20_CRATE_AND_IS_AUDITING(20, "管理平台新增审核中"),

    /**
     * 管理平台变更审核中
     */
    STATUS_21_UPDATE_AND_IS_AUDITING(21, "管理平台变更审核中"),

    /**
     * 上架审核中
     */
    STATUS_24_SHELVE_AND_IS_AUDITING(24, "上架审核中"),

    /**
     * 上架审核不通过
     */
    STATUS_25_SHELVE_AND_AUDIT_NOT_PASS(25, "上架审核不通过"),

    /**
     * 调试白名单审核
     */
    STATUS_26_WHITE_AND_IS_AUDITING(26, "调试白名单审核"),


    /**
     * 调试白名单审核不通过
     */
    STATUS_27_WHITE_AND_AUDIT_NOT_PASS(27, "调试白名单审核不通过"),


    /**
     * 在线
     */
    STATUS_30_ONLINE(30, "已上线"),


    /**
     * 已下线
     */
    STATUS_31_OFFLINE(31, "已下线"),


    /**
     * 暂停
     */
    STATUS_40_PAUSE(40, "暂停"),


    /**
     * 黑名单
     */
    STATUS_41_BAN(41, "黑名单"),


    /**
     * 已下线（关联的CSP被下线）
     */
    STATUS_42_OFFLINE_CASE_CSP(42, "已下线（关联的CSP被下线）"),


    /**
     * 调试
     */
    STATUS_50_DEBUG(50, "调试"),

    /**
     * 60 新增待审核
     * 61 变更待审核
     * 62 注销待审核
     * 63 测试转上线待审核
     * 64 新增审核不通过
     * 65 变更审核不通过
     * 66 注销审核不通过
     * 67 测试转上线审核不通过
     * 68 上线
     * 69 已注销
     * 70 测试
     * 71 下线
     */
    STATUS_60_NEW_WAIT_AUDIT(60, "新增待审核"),
    STATUS_61_EDIT_WAIT_AUDIT(61, "变更待审核"),
    STATUS_62_LOGOFF_WAIT_AUDIT(62, "注销待审核"),
    STATUS_63_TEST2ONLINE_WAIT_AUDIT(63, "测试转上线待审核"),
    STATUS_64_NEW_AUDIT_NOT_PASS(64, "新增审核不通过"),
    STATUS_65_EDIT_AUDIT_NOT_PASS(65, "变更审核不通过"),
    STATUS_66_LOGOFF_AUDIT_NOT_PASS(66, "注销审核不通过"),
    STATUS_67_TEST2ONLINE_AUDIT_NOT_PASS(67, "测试转上线审核不通过"),
    STATUS_68_ONLINE(68, "已上线"),
    STATUS_69_LOG_OFF(69, "已注销"),
    STATUS_70_TEST(70, "测试"),
    STATUS_71_OFFLINE(71, "已下线"),

    /**
     * 蜂动对接新增Chatbot状态码
     * 10 移动-未提交
     * 13 移动-处理中
     * 28 移动-已驳回
     * 43 移动-已注销
     * <p>
     * 72 电联-未提交
     * 73 电联-处理中
     * 74 电联-已驳回
     */
    STATUS_10_NO_COMMIT(10, "未提交"),
    STATUS_13_PROCESSING(13, "处理中"),
    STATUS_28_REJECT(28, "已驳回"),
    STATUS_43_LOG_OFF(43, "已注销"),

    STATUS_72_NO_COMMIT(72, "未提交"),
    STATUS_73_PROCESSING(73, "处理中"),
    STATUS_74_REJECT(74, "已驳回");


    private final Integer code;
    private final String desc;

    CSPChatbotStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CSPChatbotStatusEnum byCode(Integer code) {
        for (CSPChatbotStatusEnum value : values()) {
            if (Objects.equals(value.code, code))
                return value;
        }
        return null;
    }

    /**
     * 判断是否已注销或者下线
     *
     * @param code
     * @return
     */
    public static boolean isOfflineOrCancelable(Integer code) {
        if (ObjectUtil.isNull(code)) {
            throw new IllegalArgumentException("code为空");
        }
        return Objects.equals(STATUS_42_OFFLINE_CASE_CSP.code, code)
                || Objects.equals(STATUS_31_OFFLINE.code, code)
                || Objects.equals(STATUS_43_LOG_OFF.code, code)
                || Objects.equals(STATUS_71_OFFLINE.code, code)
                || Objects.equals(STATUS_69_LOG_OFF.code, code);
    }

    /**
     * 判断是否上线
     *
     * @param code
     * @return
     */
    public static boolean isOnline(Integer code) {
        if (ObjectUtil.isNull(code)) {
            throw new IllegalArgumentException("code为空");
        }
        return Objects.equals(STATUS_68_ONLINE.code, code) || Objects.equals(STATUS_30_ONLINE.code, code);
    }

    static final List<Integer> useableStatusList = Arrays.asList(STATUS_30_ONLINE.code, STATUS_50_DEBUG.code, STATUS_68_ONLINE.code, STATUS_70_TEST.code);

    /**
     * 返回chatbot可用的状态 ，包括 上线和测试
     *
     * @return
     */
    public static List<Integer> getUseableStatus() {
        return useableStatusList;
    }
}
