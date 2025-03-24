package com.citc.nce.authcenter.auth.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yy
 * @date 2024-05-21 10:54:05
 * 个人/企业认证数量统计响应
 */
@ApiModel("个人/企业认证数量统计响应")
@Data
public class CertStatisticsResp {
    /**
     * 个人认证数量
     */
    @ApiModelProperty(value = "个人认证数量", dataType = "int")
    Long personal;
    /**
     * 企业认证数量
     */
    @ApiModelProperty(value = "企业认证数量", dataType = "int")
    Long enterprise;
}
