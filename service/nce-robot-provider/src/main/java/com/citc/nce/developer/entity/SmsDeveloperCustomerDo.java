package com.citc.nce.developer.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author ping chen
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("developer_customer_sms")
@Accessors(chain = true)
public class SmsDeveloperCustomerDo extends BaseDo<SmsDeveloperCustomerDo> {
    private static final long serialVersionUID = 4264049361711658723L;

    @ApiModelProperty("客户登录账号")
    private String customerId;

    @ApiModelProperty("唯一标识")
    private String appId;

    @ApiModelProperty("公钥")
    private String appKey;

    @ApiModelProperty("秘钥")
    private String appSecret;

    @ApiModelProperty("回调地址")
    private String callbackUrl;

    @ApiModelProperty("接口地址")
    private String receiveUrl;

    @ApiModelProperty("状态，0:启用，1:禁用")
    private Integer state;

    @ApiModelProperty("csp账号")
    private String cspId;

    @ApiModelProperty("企业Id")
    private Long enterpriseId;
}
