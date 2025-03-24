package com.citc.nce.misc.guide.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author jcrenc
 * @since 2024/11/6 16:26
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("user_guide_progress")
@ApiModel(value = "用户引导进度")
public class UserGuideProgress {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("客户id")
    private String customerId;

    @ApiModelProperty("引导类型id")
    private Long guideId;

    @ApiModelProperty("当前步骤")
    private Integer currentStep;

    @TableField(value = "is_completed")
    @ApiModelProperty("是否完成")
    private Boolean completed;

    @ApiModelProperty("完成时间")
    private LocalDateTime completedAt;


    /*-------------------------审计字段--------------------------*/

    @ApiModelProperty("创建者")
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新人")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty("删除时间")
    @TableLogic(value = "'1000-01-01 00:00:00'", delval = "now()")
    private LocalDateTime deleteTime;
}
