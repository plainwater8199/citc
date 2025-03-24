package com.citc.nce.auth.user.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/8/18 17:57
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class PlatformApplicationReviewReq {
    @NotNull(message = "pageNo不能为空")
    @ApiModelProperty(value = "pageNo", dataType = "Integer", required = true)
    private Integer pageNo;

    @NotNull(message = "pageSize不能为空")
    @ApiModelProperty(value = "pageSize", dataType = "Integer", required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "账户名", dataType = "String", required = false)
    private String name;

    @ApiModelProperty(value = "企业名称", dataType = "String", required = false)
    private String enterpriseName;

    @ApiModelProperty(value = "申请审核状态(1 待审核 2 审核不通过 3 审核通过)", dataType = "Integer", required = false)
    private Integer approvalStatus;

    @ApiModelProperty(value = "模糊关键词", dataType = "String", required = false)
    private String keyWord;

}
