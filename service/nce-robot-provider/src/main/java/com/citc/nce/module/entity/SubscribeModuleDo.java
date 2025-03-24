package com.citc.nce.module.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("subscribe_module")
public class SubscribeModuleDo extends BaseDo<SubscribeModuleDo> implements Serializable {

    private static final long serialVersionUID = 579011216344892986L;

    @TableField(value = "subscribe_id")
    private String subscribeId; //订阅组件表uuid

    @TableField(value = "name")
    private String name; //名称

    @TableField(value = "description")
    private String description; //描述

    @TableField(value = "send_type")
    private String sendType; //发送时间类型(DAY每天/First,second,third,fourth,fifth,sixth,seventh(每周第几天))

    @TableField(value = "send_time")
//    @DateTimeFormat(pattern="HH:mm:ss")
    private String sendTime; //发送时间

    @TableField(value = "subs_success")
    private Integer subsSuccess; //订阅成功提示(0:默认提示，1:提示)

    @TableField(value = "success_5g_msg_id",updateStrategy = FieldStrategy.ALWAYS)
    private Long success5gMsgId; //订阅成功5G消息模板id

    @TableField(value = "subs_cancel")
    private Integer subsCancel; //取消订阅提示(0:默认提示，1:提示)

    @TableField(value = "cancel_5g_msg_id",updateStrategy = FieldStrategy.ALWAYS)
    private Long cancel5gMsgId; //取消订阅5G消息模板id

    @TableField(value = "subs_end")
    private Integer subsEnd; //订阅结束提示(0:默认提示，1:提示)

    @TableField(value = "end_5g_msg_id",updateStrategy = FieldStrategy.ALWAYS)
    private Long end5gMsgId; //订阅结束5G消息模板id

    @TableField(value = "deleted")
    private Integer deleted;

    @TableField(value = "delete_time")
    private Date deleteTime;

//    @TableField(value = "chatbot_id")
//    private String chatbotId;

    @TableField(value = "subscribe_status")
    private Integer subscribeStatus;//订阅组件状态0：待发送，1：发送中，2：发送完毕

}
