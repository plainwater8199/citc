package com.citc.nce.customcommand.vo;

import com.citc.nce.customcommand.enums.CustomCommandContentType;
import com.citc.nce.customcommand.enums.CustomCommandType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author jiancheng
 */
@Data
public class CustomCommandAddReq {
    @ApiModelProperty("指令名称")
    @NotBlank
    private String name;

    @ApiModelProperty("指令描述")
//    @NotBlank
    private String description;

    @ApiModelProperty("指令类型,0:定制,1:作品")
    @NotNull
    private CustomCommandType type;

    @ApiModelProperty("客户ID")
    private String customerId;

    @ApiModelProperty("指令内容类型,0:python")
    @NotNull
    private CustomCommandContentType contentType;

    @ApiModelProperty("指令内容")
    @NotBlank
    private String content;
}
