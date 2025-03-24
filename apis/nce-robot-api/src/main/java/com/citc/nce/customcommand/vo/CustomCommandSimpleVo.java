package com.citc.nce.customcommand.vo;

import com.citc.nce.customcommand.enums.CustomCommandContentType;
import com.citc.nce.customcommand.enums.CustomCommandState;
import com.citc.nce.customcommand.enums.CustomCommandType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author jiancheng
 */
@Data
public class CustomCommandSimpleVo {
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

    @ApiModelProperty("客户名称")
    private String customerName;

    @ApiModelProperty("操作内容 0:未发布,1:已发布,2:编辑已发布")
    private CustomCommandState status;

    @ApiModelProperty("启用状态")
    private Boolean active;

    @ApiModelProperty("创建时间")
    private LocalDateTime produceTime;

    @ApiModelProperty("素材ID")
    private Long mssId;
}
