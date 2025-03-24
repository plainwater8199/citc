package com.citc.nce.auth.csp.smsTemplate.vo;

import lombok.Data;

import java.util.List;

/**
 * @author ping chen
 */
@Data
public class SmsTemplateContentVo {

    /**
     * name
     */
    private String name;

    /**
     * value
     */
    private String value;

    /**
     * 变量值
     */
    private List<SmsTemplateDataVo> names;

}
