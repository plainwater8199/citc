package com.citc.nce.authcenter.sysmsg.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("system_msg_manage")
public class SystemMsgManageDo  extends BaseDo<UserMsgDetailDo> implements Serializable {
    @ApiModelProperty(value = "消息标题")
    private String title;
    @ApiModelProperty(value = "消息内容")
    private String content;
    @ApiModelProperty(value = "是否立即发送（0：否 1：是）")
    private Integer isSend;
    @ApiModelProperty(value = "发送时间")
    private Date sendTime;
    @ApiModelProperty(value = "接收对象类型，1-userId，2-用户标签")
    private Integer receiveType;
    @ApiModelProperty(value = "接收对象数组")
    private String receiveObjects;
    @ApiModelProperty(value = "是否删除")
    private Integer deleted;
    @ApiModelProperty(value = "删除时间")
    private Long deletedTime;
}
