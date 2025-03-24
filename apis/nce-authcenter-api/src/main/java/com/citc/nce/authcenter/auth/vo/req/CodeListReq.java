package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/25 18:31
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class CodeListReq {

    @ApiModelProperty(value = "code集合", dataType = "String", required = true)
    private List<String> codeList;
}
