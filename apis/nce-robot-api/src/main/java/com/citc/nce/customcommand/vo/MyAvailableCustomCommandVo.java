package com.citc.nce.customcommand.vo;

import com.citc.nce.customcommand.enums.CustomCommandContentType;
import com.citc.nce.customcommand.enums.CustomCommandType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author jiancheng
 */
@Data
public class MyAvailableCustomCommandVo {
    @ApiModelProperty("指令uuid")
    private String uuid;

    @ApiModelProperty("指令名称")
    private String name;

    @ApiModelProperty("指令类型,0:定制,1:商品")
    private CustomCommandType type;

    @ApiModelProperty("指令内容类型,0:python")
    private CustomCommandContentType contentType;

    @ApiModelProperty("指令内容")
    private String content;

    @ApiModelProperty("指令描述")
    private String description;

    @ApiModelProperty("激活")
    private Boolean active;

    @ApiModelProperty("生产时间")
    private LocalDateTime produceTime;

    @ApiModelProperty("客户ID")
    private String customerId;
}
