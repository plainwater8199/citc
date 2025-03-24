package com.citc.nce.auth.ticket.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: zouyili
 * @Contact: ylzouf
 * @Date: 2022/6/21 17:06
 * @Version: 1.0
 * @Description:根据关键词模糊查询
 */
@Data
public class GetInfoByKeyWordReq implements Serializable {
    @ApiModelProperty(value = "搜索关键词", dataType = "String", required = false)
    private String keyWord;
}
