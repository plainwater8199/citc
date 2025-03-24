package com.citc.nce.authcenter.csp.vo;

import com.citc.nce.auth.postpay.config.vo.CustomerPostpayConfigVo;
import com.citc.nce.auth.prepayment.vo.CustomerPrepaymentConfigVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author jiancheng
 */
@Data
@Accessors(chain = true)
public class CustomerUpdateReq {

    @ApiModelProperty(value = "手机号", dataType = "String", required = true)
    @NotBlank(message = "手机号不能为空")
    private String phone;

    @ApiModelProperty(value = "邮箱", dataType = "String", required = true)
    @NotBlank(message = "邮箱不能为空")
    private String mail;

    @ApiModelProperty(value = "用户id", dataType = "String", required = true)
    @NotNull
    private String userId;

    @ApiModelProperty(value = "账户名称", dataType = "String", required = true)
    @NotBlank(message = "账户名称不能为空")
    @Length(max = 20, message = "账户名称长度超过限制(最大20位)")
    private String name;

    @ApiModelProperty(value = "功能权限 1、群发；2、机器人")
    @NotBlank(message = "权限列表不能为空")
    private String permissions;

// v2.4.0计费方式取消付费方式配置
//    @ApiModelProperty("后付费配置")
//    private CustomerPostpayConfigVo postpayConfig;
//
//    @ApiModelProperty("预付费配置")
//    private CustomerPrepaymentConfigVo prepaymentConfig;
}
