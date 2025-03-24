package com.citc.nce.authcenter.auth.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author pengwanggf
 * @version 1.0
 * @project base-archetype
 * @description
 * @date 2022/8/27 08:53:12
 */
@Data
public class MerchantInfo {
    @ApiModelProperty(value = "商户ID")
    private String userId;
    @ApiModelProperty(value = "商户姓名")
    private String name;
    @ApiModelProperty(value = "商户手机")
    private String spTel;
    @ApiModelProperty(value = "商户email")
    private String spEmail;
    @ApiModelProperty(value = "商户LOGO")
    private String spLogo;
    @ApiModelProperty(value = "商户企业认证状态")
    private Integer enterpriseAuthStatus;
    @ApiModelProperty(value = "商户资历信息")
    private List<CertificateInfo> certificateInfoList;

}
