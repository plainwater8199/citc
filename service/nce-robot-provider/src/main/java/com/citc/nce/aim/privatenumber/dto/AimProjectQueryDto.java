package com.citc.nce.aim.privatenumber.dto;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/6/8 18:06
 */
@Data
public class AimProjectQueryDto extends PageParam {

    @ApiModelProperty(value = "名称/号码", dataType = "String")
    private String queryString;

    @ApiModelProperty(value = "项目状态 -1:全部 0:禁用 1:启用", dataType = "int")
    private int projectStatus;

    private Integer currentPage;

}
