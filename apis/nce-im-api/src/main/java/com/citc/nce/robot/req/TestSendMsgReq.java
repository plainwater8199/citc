package com.citc.nce.robot.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 测试发送消息
 */
@Data
public class TestSendMsgReq {

    @ApiModelProperty(value = "模板id", example = "1")
    private Long templateId;

    @ApiModelProperty(value = "chatbotId", example = "1")
    private String chatbotId;

    @ApiModelProperty(value = "电话号码", example = "1")
    @NotNull
    private String phoneNum;

    @ApiModelProperty(value = "消息发送来源 1 群发 2 机器人 3测试发送  4开发者服务发送  5组件消息")
    private Integer resourceType;

    private Long mediaTemplateId;

    private String variables;

    private Long id;

    //是否允许回落( 0 不允许, 1允许)
    private int allowFallback;

    //回落类型 1:短信 ,  2:5g阅信
    private Integer fallbackType;

    //回落短信内容(allowFallback = 1时, 必填)
    private String fallbackSmsContent;

    //允许回落阅读信模板id(fallbackType = 2时, 必填)
    private Long fallbackReadingLetterTemplateId;

    // 支付方式  1: 扣余额   2: 扣套餐
    private Integer paymentType;

}
