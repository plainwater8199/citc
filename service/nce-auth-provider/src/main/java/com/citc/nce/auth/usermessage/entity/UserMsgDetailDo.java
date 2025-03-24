package com.citc.nce.auth.usermessage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年7月15日09:59:06
 * @Version: 1.0
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_msg_detail")
public class UserMsgDetailDo extends BaseDo<UserMsgDetailDo> implements Serializable {

    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty(value = "消息Id")
    private String msgId;

    @ApiModelProperty(value = "消息标题")
    private String msgTitle;

    @ApiModelProperty(value = "用户ID")
    private String userUuid;

    @ApiModelProperty(value = "消息状态")
    private Integer msgType;

    @ApiModelProperty(value = "发送时间")
    private Date postTime;

    @ApiModelProperty(value = "查看时间")
    private Date readTime;

    @ApiModelProperty(value = "消息内容")
    private String msgDetail;

    @ApiModelProperty(value = "消息来源ID")
    private Integer sourceId;

    @ApiModelProperty(value = "业务类型(1:API 2:工单)")
    private Integer businessType;
}
