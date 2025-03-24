package com.citc.nce.auth.adminUser.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 运营人员用户的角色表
 * </p>
 *
 * @author author
 * @since 2022-09-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("admin_user_role")
@ApiModel(value = "AdminUserRole对象", description = "运营人员用户的角色表")
public class AdminUserRoleDo extends BaseDo<AdminUserRoleDo> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "角色id")
    private String roleId;

    @ApiModelProperty(value = "是否删除 默认0 未删除  1 删除")
    private Integer deleted;

    @ApiModelProperty(value = "删除时间戳")
    private Long deletedTime;


}
