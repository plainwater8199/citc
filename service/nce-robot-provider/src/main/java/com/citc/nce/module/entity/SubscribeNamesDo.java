package com.citc.nce.module.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("subscribe_names")
public class SubscribeNamesDo extends BaseDo<SubscribeNamesDo> implements Serializable {

    private static final long serialVersionUID = 6200062965149343526L;

    @TableField(value = "subscribe_id")
    private String subscribeId; //订阅组件表id

    @TableField(value = "subscribe_names_id")
    private String subscribeNamesId; // 订阅名单表uuid

    @TableField(value = "chatbot_id")
    private String chatbotId; // 机器人发短信uuid

    @TableField(value = "phone")
    private String phone; // 手机号

    @TableField(value = "advance")
    private String advance; // 当前进度(未推送,已推送[内容标题],已取消[最后推送内容标题],已结束)

    @TableField(value = "status")
    private Integer status; //订阅状态：0 未/取消订阅 1 订阅

}
