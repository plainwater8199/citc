package com.citc.nce.auth.adminUser.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/9/23 11:32
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class AdminUserResp {
    @ApiModelProperty(value = "用户id", dataType = "String")
    private String userId;

    @ApiModelProperty(value = "账户名", dataType = "String")
    private String accountName;

    @ApiModelProperty(value = "用户真实姓名", dataType = "String")
    private String fullName;

    @ApiModelProperty(value = "手机号", dataType = "String")
    private String phone;

    @ApiModelProperty(value = "用户状态(1启用,2禁用)", dataType = "Integer")
    private Integer userStatus;

    @ApiModelProperty(value = "创建者", dataType = "String")
    private String creator;

    @ApiModelProperty(value = "创建时间", dataType = "Date")
    private Date createTime;

    @ApiModelProperty(value = "更新者", dataType = "String")
    private String updater;

    @ApiModelProperty(value = "更新时间", dataType = "Date")
    private Date updateTime;
}
