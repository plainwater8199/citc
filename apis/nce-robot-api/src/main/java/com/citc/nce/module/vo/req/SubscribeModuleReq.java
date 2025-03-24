package com.citc.nce.module.vo.req;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;

@Data
public class SubscribeModuleReq implements Serializable {

    private static final long serialVersionUID = -6525770225160060553L;

    @ApiModelProperty("订阅组件表uuid")
    @NotNull
    private String subscribeId; //订阅组件表uuid

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("页码")
    private Integer pageNo = 1;

    @ApiModelProperty("页面大小")
    private Integer pageSize = 10;
}
