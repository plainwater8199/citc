package com.citc.nce.auth.user.vo.req;

import com.citc.nce.auth.adminUser.vo.req.DyzOnlyIdentificationReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 重置手机号/邮箱req
 *
 * @author huangchong
 * @date 2022/7/15 17:40
 * @describe
 */
@Data
public class ModifyUserPhoneOrEmailReq extends DyzOnlyIdentificationReq {

    @NotBlank(message = "userId不能为空")
    @ApiModelProperty(value = "userId", dataType = "String", required = true)
    private String userId;

    @NotBlank(message = "新手机号或邮箱不能为空")
    @ApiModelProperty(value = "newAccount", dataType = "String", required = true)
    private String newAccount;

    @NotNull(message = "账号类型(1--EMAIL,2--NAME,3--PHONE,4--EMAIL(激活))")
    @ApiModelProperty(value = "accountType", dataType = "Integer", required = true)
    private Integer accountType;

}
