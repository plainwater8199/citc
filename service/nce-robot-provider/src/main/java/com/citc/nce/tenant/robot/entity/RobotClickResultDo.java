package com.citc.nce.tenant.robot.entity;

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
@TableName("robot_click_result")
public class RobotClickResultDo extends BaseDo<RobotClickResultDo> implements Serializable {


    @ApiModelProperty(value = "计划节点id")
    private Long planDetailId;

    @ApiModelProperty(value = "按钮uuid")
    private String btnUuid;

    @ApiModelProperty(value = "按钮名称")
    private String btnName;

    @ApiModelProperty(value = "按钮点击数量")
    private Long clickAmount;

    @ApiModelProperty(value = "已阅数量")
    private Long readAmount;

    @ApiModelProperty(value = "是否删除,0 未删除  1 已删除")
    private Integer deleted;

    @ApiModelProperty(value = "删除时间")
    private Date deleteTime;

    @ApiModelProperty(value = "删除时间")
    private Date sendTimeDay;

    @ApiModelProperty(value = "删除时间")
    private String sendTimeHour;

    private String creatorOld;

    private String updaterOld;
}
