package com.citc.nce.h5.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Getter
@Setter
@Accessors(chain = true)
@TableName(value = "h5_form")
public class H5FormDo {

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty("h5表单id")
    private Long id ;

    @ApiModelProperty("h5应用id")
    @NotNull
    private Long h5Id;

    @ApiModelProperty("表单内容")
    private String content;

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
