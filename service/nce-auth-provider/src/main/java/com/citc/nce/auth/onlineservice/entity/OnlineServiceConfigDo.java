package com.citc.nce.auth.onlineservice.entity;

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
 * @BelongsPackage: com.citc.nce.auth.onlineservice.entity
 * @Author: litao
 * @CreateTime: 2023-01-04  14:44
 
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("online_service_config")
@ApiModel(value = "OnlineServiceConfigDo对象", description = "在线客服配置表")
public class OnlineServiceConfigDo extends BaseDo<OnlineServiceConfigDo> implements Serializable {
    @ApiModelProperty(value = "表主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "是否已启用（0：未启用 1：已启用）")
    private Integer isEnabled;

    @ApiModelProperty(value = "客服状态（0：离线 1：上线）")
    private Integer status;

    @ApiModelProperty(value = "自动回复内容")
    private String autoReplyContent;
}
