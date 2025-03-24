package com.citc.nce.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年8月15日14:35:02
 * @Version: 1.0
 * @Description: BaseReq
 */
@Data
public class BaseReq {
    @ApiModelProperty(value = "文件id",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String fileId;

    @ApiModelProperty(value = "chatBotId",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String chatBotId;

//    @NotNull
    @ApiModelProperty(value = "chatbotName",example = "联通")
    private String chatbotName;

//    @NotNull
    @ApiModelProperty(value = "appId",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String appId;

    @ApiModelProperty(value = "operator",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String operator;

    @ApiModelProperty(value = "chatbotAccountId",example = "182")
    private String chatbotAccountId;

}
