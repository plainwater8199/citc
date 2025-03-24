package com.citc.nce.auth.postpay.scheme.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author jcrenc
 * @since 2024/2/28 16:51
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("postpay_scheme")
@ApiModel(value = "PostpayScheme", description = "后付费方案")
public class PostpayScheme implements Serializable {

    private static final long serialVersionUID = 4612490299897661293L;

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty("方案名称")
    private String name;

    @ApiModelProperty("创建者")
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新者")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @ApiModelProperty("更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty("删除时间")
    @TableLogic(value = "'1000-01-01 00:00:00'", delval = "now()")
    private LocalDateTime deletedTime;
}
