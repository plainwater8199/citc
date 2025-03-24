package com.citc.nce.auth.csp.videoSms.account.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
@ApiModel
public class CspVideoSmsAccountReq extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("通道")
    private String dictCode;

    @ApiModelProperty("状态 -1:全部 0:禁用 1:启用")
    private Integer status;

    @ApiModelProperty("客户ID")
    private String customerId;
}
