package com.citc.nce.authcenter.identification.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class GetEnterpriseInfoByUserIdsReq {

    @ApiModelProperty(value = "用户id", dataType = "List")
    private List<String> userIds;
}
