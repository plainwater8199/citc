package com.citc.nce.dataStatistics.entity;


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

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("robot_media_quantity_statistics")
@ApiModel(value = "robotMediaQuantityStatistics", description = "视频短信统计")
public class RobotMediaQuantityStatisticsDo extends BaseDo<RobotMediaQuantityStatisticsDo> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @ApiModelProperty(value = "未知数量")
    private Long unknowNum;
    @ApiModelProperty(value = "成功数量")
    private Long successNum;
    @ApiModelProperty(value = "失败数量")
    private Long failedNum;
    @ApiModelProperty(value = "群发总数")
    private Long sendNum;
    @ApiModelProperty(value = "运营商")
    private String mediaOperatorCode;
    @ApiModelProperty(value = "计划id")
    private Long planId;
    @ApiModelProperty(value = "计划节点id")
    private Long planDetailId;
    @ApiModelProperty(value = "发送时间(年月日)")
    private Date sendTimeDay;
    @ApiModelProperty(value = "发送时间(小时)")
    private String sendTimeHour;
    @ApiModelProperty(value = "是否删除")
    private Integer deleted;
    @ApiModelProperty(value = "删除时间")
    private Long deleteTime;
    @ApiModelProperty(value = "富媒体账号id")
    private String mediaAccountId;
}
