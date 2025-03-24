package com.citc.nce.tenant.robot.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("short_msg_phone_result")
public class ShortMsgPhoneResultDo extends BaseDo<ShortMsgPhoneResultDo> {
    /**
     * 手机号
     */
    private String phoneNum;

    /**
     * 发送结果
     */
    private Integer sendResult;

    /**
     * 消息id
     */
    private String messageId;


    /**
     * 是否删除
     */
    private Integer deleted;

    /**
     * 删除时间
     */
    private Date deleteTime;

    /**
     * 主叫账号
     */
    private String callerAccount;

    /**
     * 来源 1 群发 2 机器人 3 测试发送， 4开发者服务
     */
    private Integer messageResource;

    /**
     * 消息内容
     */
    private String messageContent;

    /**
     * 模板id
     */
    private String templateName;

    /**
     * 节点Id
     */
    private Long planDetailId;

    /**
     * 回执时间
     */
    private Date receiptTime;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 计划id
     */
    private Long planId;

    /**
     * 最终结果
     */
    private Integer finalResult;


    private Integer messageType;

    private String shortMsgAccountId;

    private String shortMsgOperatorCode;
}
