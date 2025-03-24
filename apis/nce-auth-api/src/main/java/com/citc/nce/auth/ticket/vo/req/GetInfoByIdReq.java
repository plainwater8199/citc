package com.citc.nce.auth.ticket.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: zouyili
 * @Contact: ylzouf
 * @Date: 2022/6/21 17:06
 * @Version: 1.0
 * @Description:根据id查询
 */
@Data
public class GetInfoByIdReq implements Serializable {
    @NotNull(message = "主键id不能为空")
    @ApiModelProperty(value = "id", dataType = "Long", required = true)
    private Long id;
}
