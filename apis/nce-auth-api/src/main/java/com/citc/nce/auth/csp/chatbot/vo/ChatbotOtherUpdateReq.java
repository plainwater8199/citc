package com.citc.nce.auth.csp.chatbot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class ChatbotOtherUpdateReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    @ApiModelProperty(value = "归属客户Id")
    @Length(min = 15, max = 15, message = "客户ID必须为15位")
    private String customerId;

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
}
