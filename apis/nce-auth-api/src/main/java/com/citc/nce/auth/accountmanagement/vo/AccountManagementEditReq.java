package com.citc.nce.auth.accountmanagement.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: yangchuang
 * @Date: 2022/8/9 17:03
 * @Version: 1.0
 * @Description:
 */
@Data
public class AccountManagementEditReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "账号名称")
    private String accountName;

    @ApiModelProperty("chatbot id")
    private String chatbotAccount;

    @ApiModelProperty(value = "appid")
    private String appId;

    @ApiModelProperty(value = "app key")
    private String appKey;

    @ApiModelProperty(value = "token")
    private String token;

    @ApiModelProperty(value = "消息地址")
    private String messageAddress;

    @ApiModelProperty(value = "文件地址")
    private String fileAddress;

    private Integer chatbotStatus;

}
