package com.citc.nce.auth.onlineservice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @BelongsPackage: com.citc.nce.auth.onlineservice.entity
 * @Author: litao
 * @CreateTime: 2023-01-06  09:58
 
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("mc_session_message_record")
@ApiModel(value = "McSessionMessageRecordDo对象", description = "会话消息记录表")
public class McSessionMessageRecordDo implements Serializable {
    @ApiModelProperty(value = "表主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "发送人id")
    private String senderId;

    @ApiModelProperty(value = "发送人昵称")
    private String senderName;

    @ApiModelProperty(value = "发送人头像")
    private String senderHeadImg;

    @ApiModelProperty(value = "接收人id")
    private String receiverId;

    @ApiModelProperty(value = "发送时间")
    private String sendTime;

    @ApiModelProperty(value = "发送内容")
    private String content;

    @ApiModelProperty(value = "消息状态 0 已读 1 未读")
    private String readStatus;

    @ApiModelProperty(value = "软删除键(0未删除;1已删除)")
    private String flag;

    @ApiModelProperty(value = "撤回时间")
    private String withdrawTime;

    @ApiModelProperty(value = "回应时间")
    private String responseTime;

    @ApiModelProperty(value = "会话id")
    private String sessionId;

    @ApiModelProperty(value = "会话内消息排序")
    private String msgSort;

    @ApiModelProperty(value = "类型(文本、图片、链接等)")
    private String msgType;

    @ApiModelProperty(value = "扩展字段信息")
    private String ext;
}
