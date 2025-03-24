package com.citc.nce.robot.vo;

import lombok.Data;
/**
 * @author ping chen
 */
@Data
public class SmsTemplateAuditStatus {
    /**
     * 平台模板Id
     */
    private String templateId;
    /**
     * 审核状态 0：待审核;1：通过;2：审核失败;-1：模板不存在
     */
    private Integer status;
}
