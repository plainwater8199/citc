package com.citc.nce.authcenter.userLoginRecord.vo;

import com.citc.nce.common.core.pojo.PageParam;
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
public class QueryUserLoginRecordReq extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "开始时间", dataType = "String")
    private String startTime;

    @ApiModelProperty(value = "结束时间", dataType = "String")
    private String endTime;

    @ApiModelProperty(value = "userId", dataType = "String")
    private String userId;

    @ApiModelProperty(value = "登录用户所属CSP账号", dataType = "String")
    private String cspAccount;

    @ApiModelProperty(value = "平台信息1:核能商城客户端 2:chatbot客户端 3:硬核桃社区 4:管理平台", dataType = "Integer")
    private Integer platformType;
}
