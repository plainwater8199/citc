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
public class ProcessQuantitySumResp {

    @ApiModelProperty(value = "场景总数量", dataType = "Long")
    private Long scenceNum;

    @ApiModelProperty(value = "流程总数量", dataType = "Long")
    private Long processNum;

}
