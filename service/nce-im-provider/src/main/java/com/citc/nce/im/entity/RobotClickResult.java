package com.citc.nce.im.entity;

import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * @description: 时间段的按钮的点击数
 * @author: zhujy
 * @date: Created in 2024/3/11 16:20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class RobotClickResult extends BaseDo<RobotClickResult> implements Serializable {


    @ApiModelProperty(value = "计划节点id")
    private Long planDetailId;

    @ApiModelProperty(value = "按钮uuid")
    private String btnUuid;

    /** 悬浮按钮 **/
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

    @ApiModelProperty(value = "发送日期")
    private Date sendTimeDay;

    @ApiModelProperty(value = "发送时间段")
    private String sendTimeHour;
}
