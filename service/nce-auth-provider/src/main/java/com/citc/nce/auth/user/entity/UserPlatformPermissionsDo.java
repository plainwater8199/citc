package com.citc.nce.auth.user.entity;

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
import java.util.Date;

/**
 * <p>
 * 用户平台权限表
 * </p>
 *
 * @author author
 * @since 2022-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_platform_permissions")
@ApiModel(value = "UserPlatformPermissions对象", description = "用户平台权限表")
public class UserPlatformPermissionsDo extends BaseDo<UserPlatformPermissionsDo> implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "表主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "用户状态(0初始化 默认未开启,1启用,2禁用)")
    private Integer userStatus;

    @ApiModelProperty(value = "申请时间")
    private Date applyTime;

    @ApiModelProperty(value = "申请审核状态(1 待审核 2 审核不通过 3 审核通过)")
    private Integer approvalStatus;

    @ApiModelProperty(value = "平台信息(1核能商城 2硬核桃 3chatbot)")
    private Integer protal;

    @ApiModelProperty(value = "审核记录id")
    private String approvalLogId;

    @ApiModelProperty(value = "是否删除 默认0 未删除  1 删除")
    private Integer deleted;

    @ApiModelProperty(value = "删除时间戳")
    private Long deletedTime;


}
