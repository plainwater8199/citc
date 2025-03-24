package com.citc.nce.module.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("subscribe_content")
public class SubscribeContentDo extends BaseDo<SubscribeContentDo> implements Serializable {
    private static final long serialVersionUID = -875220846488988219L;

    @TableField(value = "subscribe_id")
    private String subscribeId;   //订阅组件表id

    @TableField(value = "sub_content_id")
    private String subContentId; // 订阅内容表uuid

    @TableField(value = "title")
    private String title;   // 标题

    @TableField(value = "msg_5g_id")
    private Long msg5gId;   //5G消息模板id

    @TableField(value = "content")
    private String content; //内容

    @TableField(value = "subscribe_content_order")
    private Integer subscribeContentOrder; //订阅顺序

    @TableField(value = "subscribe_content_status")
    private Integer subscribeContentStatus; //订阅内容状态0：待发送，1：已发送，2：取消发送

    @TableField(value = "is_the_last")
    private Integer isTheLast;

    @TableField(value = "deleted")
    private Integer deleted;

    @TableField(value = "deleted_time")
    private Date deletedTime;

}
