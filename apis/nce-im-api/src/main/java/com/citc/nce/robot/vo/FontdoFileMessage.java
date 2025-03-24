package com.citc.nce.robot.vo;

import com.citc.nce.robot.vo.directcustomer.FallbackSms;
import com.citc.nce.robot.vo.directcustomer.FallbackTemplate;
import com.citc.nce.robot.vo.directcustomer.Parameter;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class FontdoFileMessage extends BaseMessage {
    //唯一标识一个聊天会话
    private String supplierTag;
    /**
     * 消息id,用于追溯
     */
    String messageId;
    /**
     * 群发计划detailId
     */
    String groupSendPlanDetailId;
    /**
     * 唯一标识会话
     */
    String conversationId;

    /**
     * 模板类型
     * text 纯文本消息
     * RCS 5G消息模板
     * AIM 智能短信模板
     * MMS 视频短信模板
     * @See nut.rcs.gateway.consts.SupplierTemplateTypeEnum
     */
    String templateType;

    /**
     * 模板ID，templateType为RCS，AIM，MMS时必选
     */
    Long templateId;

    /**
     * 直发文本内容，templateType为TEXT类型必选
     */
    String text;

    /**
     * 群发号码列表
     */
    @NotEmpty
    List<String> numbers;

    /**
     * 模版参数，动态参数模板必填
     */
    List<Parameter> params;

//    如果发送的是5G消息，只允许设置智能短信回落，视频短信回落，文本短信回落；
//    如果发送的是智能短信消息，只允许设置视频短信回落，文本短信回落；
//    如果发送的是视频短信消息，只允许设置文本短信回落；
//    如果发送的是文本短信，不允许设置回落；

    /**
     * 智能短信回落
     */
    FallbackTemplate fallbackAim;

    /**
     * 视频短信回落
     */
    FallbackTemplate fallbackMms;

    /**
     * 文本短信回落
     */
    FallbackSms fallbackSms;
}
