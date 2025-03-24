package com.citc.nce.h5.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class H5QueryVO {

    @ApiModelProperty(value = "当前页",example = "1")
    private Integer pageNo;

    @ApiModelProperty(value = "每页展示条数",example = "5")
    private Integer pageSize;

    @ApiModelProperty(value = "0草稿 1是在线 2是已下线  不传：返回全部状态")
    private Integer status;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "关键词")
    private String keyword;

    @ApiModelProperty(value = "用户Id")
    private String customerId;

    @ApiModelProperty(value = "应用ID")
    private String applicationId;

    @ApiModelProperty(value = "手机号")
    private String phone;

}
