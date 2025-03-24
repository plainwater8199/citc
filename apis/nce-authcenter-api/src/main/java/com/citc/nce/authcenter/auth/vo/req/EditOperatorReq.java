package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@Accessors(chain = true)
public class EditOperatorReq {
    @ApiModelProperty(value = "用户id", dataType = "String", required = false)
    private String userId;

    @Length(max = 25, message = "账户名长度超过限制")
    @NotBlank(message = "账户名不能为空")
    @ApiModelProperty(value = "账户名", dataType = "String", required = true)
    private String accountName;

    @Length(max = 25, message = "姓名长度超过限制")
    @NotBlank(message = "姓名不能为空")
    @ApiModelProperty(value = "姓名", dataType = "String", required = true)
    private String fullName;

    @NotBlank(message = "手机号不能为空")
    @ApiModelProperty(value = "手机号", dataType = "String", required = true)
    private String phone;

    @ApiModelProperty(value = "角色id", dataType = "String", required = false)
    private List<String> roleIdList;
}
