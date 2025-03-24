package com.citc.nce.robot.api.mall.template.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>扩展商城-商品 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class MallTemplateUpdateReq extends MallTemplateSaveReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "模板id", dataType = "String", required = true)
    @NotBlank(message = "模板id不能为空")
    private String templateId;

}
