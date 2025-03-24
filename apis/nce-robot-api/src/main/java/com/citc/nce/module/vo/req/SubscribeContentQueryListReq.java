package com.citc.nce.module.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SubscribeContentQueryListReq implements Serializable {
    private static final long serialVersionUID = -7279184138751029380L;


    @ApiModelProperty("订阅组件表uuid")
    @NotNull(message = "订阅组件uuid不能为空")
    private String subscribeId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("页码")
    private Integer pageNo = 1;

    @ApiModelProperty("页面大小")
    private Integer pageSize = 10;

}
