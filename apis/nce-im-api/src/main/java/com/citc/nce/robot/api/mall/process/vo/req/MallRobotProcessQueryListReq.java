package com.citc.nce.robot.api.mall.process.vo.req;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>扩展商城-商品 新增</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 17:44
 */
@Data
public class MallRobotProcessQueryListReq extends PageParam implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "模板uuid", dataType = "String")
    private String templateId;
}
