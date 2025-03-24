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
@TableName(value = "h5_of_summery")
public class H5OfSummeryDo {

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty("应用id")
    private Long id ;

    @ApiModelProperty("H5应用id")
    private Long h5Id ;

    @ApiModelProperty("标题")
    private String title;
    /**
     * csp和客户都可以创建表单，谁创建的customerId和creator就是谁，但是csp创建表单后可以售卖，客户购买了售卖的表单后会从原表单copy一份把customerId设置成客户id，
     * 所以存在customerId和creator都是客户id或者都是csp id和creator是csp id但是customerId是客户id三种情况，查询当前用户表单时应该使用customerId进行筛选
     */
    @ApiModelProperty("用户唯一id")
    private String customerId;

    @ApiModelProperty("描述")
    private String h5Desc;

    @ApiModelProperty("页面类型：1长页 100翻页")
    private Integer pageDisplay;

    @ApiModelProperty("翻页类型")
    private String flipType;

    @ApiModelProperty("关联活动id")
    private Long msActivityId;

    @ApiModelProperty("缩略图")
    private String formCover;

    @ApiModelProperty("模版schema-json")
    private String tpl;

    @ApiModelProperty("全局样式")
    private String globalStyle;

    @ApiModelProperty("状态  0草稿 1是在线 2是已下线")
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

    @ApiModelProperty("素材作品ID")
    private Long mssId;

}
