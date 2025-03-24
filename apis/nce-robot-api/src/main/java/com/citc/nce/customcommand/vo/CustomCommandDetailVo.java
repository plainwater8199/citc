package com.citc.nce.customcommand.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.citc.nce.customcommand.enums.CustomCommandContentType;
import com.citc.nce.customcommand.enums.CustomCommandState;
import com.citc.nce.customcommand.enums.CustomCommandType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author jiancheng
 */
@Data
public class CustomCommandDetailVo {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @ApiModelProperty("指令uuid")
    private String uuid;

    @ApiModelProperty("指令名称")
    private String name;

    @ApiModelProperty("指令描述")
    private String description;

    @ApiModelProperty("指令类型,0:定制,1:商品")
    private CustomCommandType type;

    @ApiModelProperty("客户ID")
    private String customerId;

    @ApiModelProperty("指令内容类型,0:python")
    private CustomCommandContentType contentType;

    @ApiModelProperty("指令内容")
    private String content;

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("操作内容 0:未发布,1:已发布,2:编辑已发布")
    private CustomCommandState status;

    @ApiModelProperty("素材ID")
    private Long mssId;

    @ApiModelProperty("启用状态")
    private Boolean active;

    @ApiModelProperty("生产时间")
    private LocalDateTime produceTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}
