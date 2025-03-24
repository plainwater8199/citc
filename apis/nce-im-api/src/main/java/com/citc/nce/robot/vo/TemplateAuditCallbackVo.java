package com.citc.nce.robot.vo;

import lombok.Data;

/**
 * 亿美软通富媒体短信接收模板状态报备参数
 *
 * @author jiancheng
 */
@Data
public class TemplateAuditCallbackVo {
    private String templateNumber;
    private Integer cmccState;
    private String cmccReason;
    private Integer cuccState;
    private String cuccReason;
    private Integer ctccState;
    private String ctccReason;
}
