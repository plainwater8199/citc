package com.citc.nce.module.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ImportContactGroupReq {

    @NotNull(message="组件类型不能为空")
    @ApiModelProperty("组件类型:订阅51  打卡52")
    private Integer moduleType;
    @NotNull(message="组件ID不能为空")
    @ApiModelProperty("组件ID")
    private String moduleId;
    @ApiModelProperty("联系人组：如果为null表示创建新的联系人组")
    private Long groupId;
}
