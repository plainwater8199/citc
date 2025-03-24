package com.citc.nce.auth.user.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/18 19:49
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class KeyWordReq {

    @ApiModelProperty(value = "keyWord", dataType = "String", required = true)
    private String keyWord;
}
