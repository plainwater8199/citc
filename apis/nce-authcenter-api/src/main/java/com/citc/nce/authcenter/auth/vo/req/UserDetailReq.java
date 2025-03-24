package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/21 17:07
 * @Version: 1.0
 * @Description:
 */
@Data
public class UserDetailReq implements Serializable {
    @ApiModelProperty(value = "userId", dataType = "String")
    private String id;
}
