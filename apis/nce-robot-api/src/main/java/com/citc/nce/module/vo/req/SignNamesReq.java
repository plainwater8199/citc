package com.citc.nce.module.vo.req;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
public class SignNamesReq extends PageParam implements Serializable {

    private static final long serialVersionUID = 3786129905396611523L;

    @ApiModelProperty("打卡名单信息ID")
    private Long id;

    @ApiModelProperty(value = "打卡组件表uuid")
    @NotNull(message = "打卡组件表uuid！")
    private String signModuleId;

    @ApiModelProperty(value = "打卡名单表uuid")
    private String signNamesId;

    @ApiModelProperty(value = "机器人发短信uuid")
    private String chatbotId; // 机器人发短信uuid

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("打卡次数")
    private Long signCount;

    @ApiModelProperty("参加打卡时间")
    private Date signeDate;

}
