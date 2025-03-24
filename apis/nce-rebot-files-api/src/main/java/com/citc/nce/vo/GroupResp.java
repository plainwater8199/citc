package com.citc.nce.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: IPictureService
 */
@Data
public class GroupResp implements Serializable {

    private static final long serialVersionUID = 7354754147224215501L;

    @ApiModelProperty(value = "分组id",example = "2")
    private Long id;

    @ApiModelProperty(value = "分组名称",example = "动物类")
    private String groupName;

    @ApiModelProperty(value = "分组数量",example = "3")
    private Integer num;
}