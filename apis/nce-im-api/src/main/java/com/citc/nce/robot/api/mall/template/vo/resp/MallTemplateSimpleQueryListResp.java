package com.citc.nce.robot.api.mall.template.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * <p>挂短-项目 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class MallTemplateSimpleQueryListResp {

    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;

    @ApiModelProperty(value = "模板名称", dataType = "String")
    private String templateName;

    @ApiModelProperty(value = "模板uuid", dataType = "String")
    private String templateId;
}
