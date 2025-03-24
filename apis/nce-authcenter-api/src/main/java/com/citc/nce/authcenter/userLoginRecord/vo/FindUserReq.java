package com.citc.nce.authcenter.userLoginRecord.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * @BelongsPackage: 用户登陆记录查询请求体
 * @Author: hhluop
 * @CreateTime: 2023-03-02  9:41
 * @Description:
 * @Version: 1.0
 */
@Data
public class FindUserReq extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户id不能为空")
    @ApiModelProperty(value = "userId", dataType = "String")
    private String userId;

    @NotNull(message = "平台信息不能为空")
    @ApiModelProperty(value = "平台信息1:核能商城客户端 2:chatbot客户端 3:硬核桃社区 4:管理平台", dataType = "Integer")
    private Integer platformType;
}
