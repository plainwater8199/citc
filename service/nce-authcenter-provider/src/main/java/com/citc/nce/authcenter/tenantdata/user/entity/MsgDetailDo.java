package com.citc.nce.authcenter.tenantdata.user.entity;

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
import java.util.Date;

/**
 * @Author: ylzouf
 * @Contact: ylzouf@isoftstone.com
 * @Date: 2022/07/15
 * @Version 1.0
 * @Description:
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_msg_detail")
@ApiModel(value = "MsgDetailDo对象", description = "用户消息详情表")
public class MsgDetailDo extends BaseDo<MsgDetailDo> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "是否删除")
    private Integer deleted;

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


}
