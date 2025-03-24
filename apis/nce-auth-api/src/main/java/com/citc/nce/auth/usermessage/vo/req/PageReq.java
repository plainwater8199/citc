package com.citc.nce.auth.usermessage.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: GroupDto
 */
@Data
@Accessors(chain = true)
public class PageReq {
    @NotNull
    @ApiModelProperty(value = "用户id", dataType = "String")
    private String userId;
    @ApiModelProperty(value = "标题",dataType = "String")
    private String title;

    @NotNull
    @ApiModelProperty(value = "当前页",dataType = "Integer", example = "1")
    private Integer pageNo;

    @NotNull
    @ApiModelProperty(value = "每页展示条数",dataType = "Integer", example = "5")
    private Integer pageSize;
}
