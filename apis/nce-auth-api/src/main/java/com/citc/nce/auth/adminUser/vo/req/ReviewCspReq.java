package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/18 19:48
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class ReviewCspReq {

    @NotBlank(message = "userId不能为空")
    @ApiModelProperty(value = "客户端用户userId", dataType = "String", required = true)
    private String userId;

    @ApiModelProperty(value = "申请审核状态(1 待审核 2 审核不通过 3 审核通过)", dataType = "Integer", required = false)
    private Integer approvalStatus;

    @NotNull(message = "protal不能为空")
    @ApiModelProperty(value = "平台信息(1核能商城2硬核桃3chatbot)", dataType = "Integer", required = true)
    private Integer protal;

    @NotBlank(message = "approvalLogId不能为空")
    @ApiModelProperty(value = "审核记录id", dataType = "String", required = true)
    private String approvalLogId;

    @ApiModelProperty(value = "备注", dataType = "String", required = false)
    private String remark;

    @ApiModelProperty(value = "被更新用户Id", dataType = "String", required = false)
    private String updateTargetUserId;
}
