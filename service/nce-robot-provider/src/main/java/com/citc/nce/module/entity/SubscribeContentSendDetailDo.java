package com.citc.nce.module.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("subscribe_content_send_detail")
public class SubscribeContentSendDetailDo extends BaseDo<SubscribeContentSendDetailDo> implements Serializable {

    private static final long serialVersionUID = -1903030959574923319L;

    @TableField(value = "subscribe_id")
    private String subscribeId;   //订阅组件表id

    @TableField(value = "sub_content_id")
    private String subContentId; // 订阅内容表uuid

    @TableField(value = "title")
    private String title;   // 标题

    @TableField(value = "msg_5g_id")
    private Long msg5gId;   //5G消息模板id

    @TableField(value = "phone")
    private String phone; //手机号

    @TableField(value = "subscribe_send_time")
    private Date subscribeSendTime; //发送时间

    @TableField(value = "status")
    private Integer status; //发送状态：0:未发送 1:已发送 2:取消发送

}
