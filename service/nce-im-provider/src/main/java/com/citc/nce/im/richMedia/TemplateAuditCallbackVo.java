package com.citc.nce.im.richMedia;

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
    private String cmccStateReason;
    private Integer cuccState;
    private String cuccStateReason;
    private Integer ctccState;
    private String ctccStateReason;
}
