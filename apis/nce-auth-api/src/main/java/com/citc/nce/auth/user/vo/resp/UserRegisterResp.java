package com.citc.nce.auth.user.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/21 17:09
 * @Version: 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class UserRegisterResp {

    @ApiModelProperty(value = "主键id", dataType = "Long")
    private Long id;
    /**
     * 用户id
     */
    @ApiModelProperty(value = "用户id", dataType = "String")
    private String userId;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名", dataType = "String")
    private String name;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号", dataType = "String")
    private String phone;

    /**
     * 电子邮箱
     */
    @ApiModelProperty(value = "电子邮箱", dataType = "String")
    private String mail;
}
