package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/31 16:57
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ProcessQuantityTopResp {

    @ApiModelProperty(value = "场景正序", dataType = "List")
    private List<?> scenceListAsc;

    @ApiModelProperty(value = "场景倒序", dataType = "List")
    private List<?> scenceListDesc;

    @ApiModelProperty(value = "流程正序", dataType = "List")
    private List<?> processListAsc;

    @ApiModelProperty(value = "流程倒序", dataType = "List")
    private List<?> processListDesc;

}
