package com.citc.nce.dataStatistics.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/31 17:00
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class SortReq{

    @ApiModelProperty(value = "5G账户id", dataType = "String", required = false)
    private String chatBotId;

}
