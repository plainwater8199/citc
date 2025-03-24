package com.citc.nce.authcenter.userLoginRecord.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/2/27 10:43
 */
@Data
public class QueryUserLoginRecordResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;

    @ApiModelProperty(value = "userId", dataType = "String")
    private String userId;

    @ApiModelProperty(value = "登录用户所属CSP账号", dataType = "String")
    private String cspAccount;

    @ApiModelProperty(value = "用户每日首次登录时间", dataType = "Date")
    private Date dailyFirstLoginTime;

    @ApiModelProperty(value = "创建时间", dataType = "Date")
    private Date createTime;

    @ApiModelProperty(value = "平台信息1:核能商城客户端 2:chatbot客户端 3:硬核桃社区 4:管理平台", dataType = "Integer")
    private Integer platformType;
}
