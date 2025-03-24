package com.citc.nce.misc.schedule.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 每日流程统计表
 * </p>
 *
 * @author author
 * @since 2022-10-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("schedule_execute_log")
@ApiModel(value = "schedule_execute_log对象", description = "定时任务执行记录表")
public class ScheduleExecuteLogDo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "定时任务名")
    private String scheduleName;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "消息,执行结果")
    private String result;

    @ApiModelProperty(value = "执行时间")
    private Date createTime;

    @ApiModelProperty(value = "创建者")
    private String creator;

}
