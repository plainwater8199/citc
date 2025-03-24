package com.citc.nce.authcenter.auth.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/9/7 20:13
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class StatusAndProtalResp {

    @ApiModelProperty(value = "用户状态(0初始化 默认未开启,1启用,2禁用)", dataType = "Integer")
    private Integer userStatus;

    @ApiModelProperty(value = "申请审核状态(1 待审核 2 审核不通过 3 审核通过)", dataType = "Integer")
    private Integer approvalStatus;

    @ApiModelProperty(value = "平台信息(0统一用户管理平台1核能商城2硬核桃3chatbot)", dataType = "Integer")
    private Integer protal;
}
