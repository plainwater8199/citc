package com.citc.nce.auth.adminUser.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author huangchong
 * @description 后台管理用户表
 * @date 2022-07-20
 */
@Data
@Accessors(chain = true)
@TableName("admin_user")
public class AdminUserDo extends BaseDo<AdminUserDo> implements Serializable {

    private static final long serialVersionUID = 1L;

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

    @ApiModelProperty(value = "是否删除 默认0 未删除 1 删除", dataType = "Integer")
    private Integer deleted;

    @ApiModelProperty(value = "删除时间戳", dataType = "Long")
    private Long deletedTime;

}
