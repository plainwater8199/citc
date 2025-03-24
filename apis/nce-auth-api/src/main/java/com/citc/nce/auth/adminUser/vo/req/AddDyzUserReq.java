package com.citc.nce.auth.adminUser.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/11/9 16:31
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class AddDyzUserReq {
    @ApiModelProperty(value = "资产标识", dataType = "String", required = true)
    private String appCode;
    @ApiModelProperty(value = "签名", dataType = "String", required = true)
    private String sign;
    @ApiModelProperty(value = "业务流水号", dataType = "String", required = true)
    private String bizSn;
    @ApiModelProperty(value = "人员类型 1:自有人员 2:三方人员 3:外协人员 4:人脸识别用户", dataType = "String", required = true)
    private String userType;
    @ApiModelProperty(value = "所属机构", dataType = "String", required = true)
    private String belongOrg;
    @ApiModelProperty(value = "手机号", dataType = "String", required = true)
    private String phoneNumber;
    @ApiModelProperty(value = "证件类型，1身份证，7是其它，默认传7", dataType = "String", required = true)
    private String docType;
    @ApiModelProperty(value = "邮箱", dataType = "String", required = false)
    private String email;
    @ApiModelProperty(value = "用户中心id", dataType = "String", required = true)
    private String userAccount;
}
