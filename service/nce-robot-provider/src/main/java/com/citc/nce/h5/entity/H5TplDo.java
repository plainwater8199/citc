package com.citc.nce.h5.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "h5_tpl")
public class H5TplDo {

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty("h5模版id")
    private Long id ;

    @ApiModelProperty("h5应用id")
    private Long h5Id;

    @ApiModelProperty("用户唯一id")
    private String customerId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("描述")
    private String tplDesc;

    @ApiModelProperty("分享图标")
    private String shareIcon;

    @ApiModelProperty("模版schema-json")
    private String tpl;

    @ApiModelProperty("全局样式")
    private String globalStyle;

    @ApiModelProperty("0草稿 1是在线 2是已下线")
    private Integer status;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("创建者")
    private String creator;

    @ApiModelProperty("更新者")
    private String updater;

    @ApiModelProperty("删除标识")
    private Integer deleted;

}
