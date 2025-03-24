package com.citc.nce.robot.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author jcrenc
 * @since 2024/5/16 14:43
 */
@Data
@Accessors(chain = true)
public class SmsSendParam {
    private String accountId;
    private Long templateId;
    private String variableStr;
    private List<TemplateSmsIdAndMobile> mobiles;
    private Integer resourceType;
    private Integer paymentType;
}
