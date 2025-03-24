package com.citc.nce.authcenter.userLoginRecord.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsPackage: 用户登陆记录查询返回体
 * @Author: hhluop
 * @CreateTime: 2023-03-02  9:41
 * @Description:
 * @Version: 1.0
 */
@Data
public class FindUserResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;

    @ApiModelProperty(value = "userId", dataType = "String")
    private String userId;

    @ApiModelProperty(value = "平台信息1:核能商城客户端 2:chatbot客户端 3:硬核桃社区 4:管理平台", dataType = "Integer")
    private Integer platformType;

    @ApiModelProperty(value = "法务文件版本号", dataType = "Integer")
    private Integer version;
}
