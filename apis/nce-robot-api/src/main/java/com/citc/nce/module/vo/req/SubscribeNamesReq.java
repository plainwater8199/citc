package com.citc.nce.module.vo.req;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
public class SubscribeNamesReq extends PageParam implements Serializable {

    private static final long serialVersionUID = 1310922301534330972L;

    @ApiModelProperty("订阅名单信息ID")
    private Long id;

    @ApiModelProperty(value = "订阅组件表uuid")
    private String subscribeId;

    @ApiModelProperty(value = "订阅名单表uuid")
    private String subscribeNamesId; // 订阅名单表uuid

    @ApiModelProperty(value = "机器人发短信uuid")
    private String chatbotId; // 机器人发短信uuid

    @ApiModelProperty(value = "当前进度")
    private String advance;

    @ApiModelProperty("手机号")
    @NotNull(message = "手机号不能为空！")
    private String phone;

    @ApiModelProperty("订阅状态")
    private Integer status;

    @ApiModelProperty("订阅时间")
    private Date subscribeDate;

}
