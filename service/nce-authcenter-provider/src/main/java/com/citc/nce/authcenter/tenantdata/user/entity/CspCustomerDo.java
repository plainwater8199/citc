package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author jiancheng
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("csp_customer")
public class CspCustomerDo extends BaseDo<CspCustomerDo> implements Serializable {

    private static final long serialVersionUID = 571041720613611041L;

    @ApiModelProperty("csp id")
    private String cspId;

    @ApiModelProperty("客户ID")
    private String customerId;

    @ApiModelProperty("客户名")
    private String name;

    @ApiModelProperty("用户头像UUID")
    private String userImgUuid;

    private String phone;

    private String mail;

    private Integer emailActivated;

    private Integer personAuthStatus;

    private Integer enterpriseAuthStatus;

    private Integer authStatus;

    private String province;

    private String provinceCode;

    private String city;

    private String cityCode;

    private String area;

    private String areaCode;

    private Integer customerActive;

    private String permissions;

    private Integer isBinding;

    private Integer isBindingChatbot;

    @ApiModelProperty("是否已删除")
    private Integer deleted;

    @ApiModelProperty("删除时间")
    private Long deletedTime;


}
