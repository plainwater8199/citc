package com.citc.nce.robot.api.mall.template.vo.req;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>挂短-项目 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class MallTemplateBaseQueryReq extends PageParam {

    @ApiModelProperty(value = "模板名称", dataType = "String")
    private String templateName;
}
