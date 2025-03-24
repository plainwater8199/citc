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
@TableName(value = "h5_data_source")
public class H5DataSourceDo {

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty("应用id")
    private Long id ;

    @ApiModelProperty("数据源字段")
    private String field;

    @ApiModelProperty("数据源名称")
    private String name;

    @ApiModelProperty("数据类型")
    private Integer type;

    @ApiModelProperty("数据")
    private String data;

    @ApiModelProperty("数据")
    private String dataId;

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
