package com.citc.nce.tenant.robot.entity;

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
import java.math.BigDecimal;
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
@TableName("process_quantity_days")
@ApiModel(value = "ProcessQuantityDays对象", description = "每日流程统计表")
public class ProcessQuantityDaysDo extends BaseDo<ProcessQuantityDaysDo> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "运营商类型")
    private Integer operatorType;

    @ApiModelProperty(value = "账户id")
    private String chatbotId;

    @ApiModelProperty(value = "场景id")
    private Long robotSceneNodeId;

    @ApiModelProperty(value = "流程id")
    private Long robotProcessSettingNodeId;

    @ApiModelProperty(value = "流程触发数量")
    private Long processTriggersNum;

    @ApiModelProperty(value = "流程完成数量")
    private Long processCompletedNum;

    @ApiModelProperty(value = "兜底回复数量")
    private Long bottomReturnNum;

    @ApiModelProperty(value = "流程完成率")
    private BigDecimal processCompletionRate;

    @ApiModelProperty(value = "时间(每天)")
    private Date days;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "是否删除 默认0 未删除  1 删除")
    private Integer deleted;

    @ApiModelProperty(value = "删除时间戳")
    private Long deletedTime;
}
