package com.citc.nce.auth.messagetemplate.vo;

import lombok.Data;

import java.util.List;

/**
 * @author yy
 * @date 2024-03-09 18:57:41
 */
@Data
public class SupportDetailsReq {
    /**
     * 参考运营商说明模板id
     */
    String templateId;
    /**
     * 参考运营商说明
     * WHOLE 全⽹
     * CMCC 移动
     * CUCC 联通
     * CTCC 电信
     */
    String carrierType;
    /**
     * 审核状态， PENDING,SUCCESS,FAILED
     */
    String auditStatus;
    /**
     * 审核备注
     */
    String auditRemark;
    /**
     * 审核时间
     */
    String auditDate;

    List<TemplateSupportInfoReq> supports;
}
