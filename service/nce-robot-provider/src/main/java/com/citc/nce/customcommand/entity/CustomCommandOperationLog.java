package com.citc.nce.customcommand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.citc.nce.customcommand.enums.CustomCommandOperation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 自定义指令操作日志
 * </p>
 *
 * @author jcrenc
 * @since 2023-11-09 02:53:48
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("custom_command_operation_log")
@ApiModel(value = "CustomCommandOperationLog对象", description = "自定义指令操作日志")
public class CustomCommandOperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("自定义指令ID")
    private Long customCommandId;

    @ApiModelProperty("自定义指令uuid")
    private String customCommandUuid;

    @ApiModelProperty("操作时间")
    private LocalDateTime operateTime;

    @ApiModelProperty("操作内容 0:新增,1:编辑,2:发布,3:还原,4:开启,5:关闭")
    private CustomCommandOperation operation;

    @ApiModelProperty("处理人员名称")
    private String operatorName;

    @ApiModelProperty("操作人员ID")
    private String operatorId;

    @ApiModelProperty("备注")
    private String note;


}
