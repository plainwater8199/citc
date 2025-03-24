package com.citc.nce.authcenter.csp.vo;

import com.citc.nce.common.core.enums.CustomerPayType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author yy
 */
@Data
public class UserInfoForDropdownVo {
    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;

    @ApiModelProperty("用户数据")
    private String userId;

    @ApiModelProperty("csp id")
    private String cspId;

    @ApiModelProperty("客户ID")
    private String customerId;

    @ApiModelProperty("邮箱")
    private String mail;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("账户名")
    private String name;

    @ApiModelProperty("企业名称")
    private String enterpriseName;
}
