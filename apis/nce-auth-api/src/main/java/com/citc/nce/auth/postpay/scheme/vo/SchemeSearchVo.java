package com.citc.nce.auth.postpay.scheme.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author jcrenc
 * @since 2024/3/6 11:20
 */
@Data
public class SchemeSearchVo extends PageParam {
    @ApiModelProperty("方案名称 模糊查询")
    private String name;
}
