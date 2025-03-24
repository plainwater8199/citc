package com.citc.nce.auth.csp.chatbot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class checkForCanCreateChatbotReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 归属客户Id
     */
    @ApiModelProperty(value = "归属客户Id")
    @NotNull(message = "归属客户Id不能为空")
    @Length(min = 15, max = 15, message = "客户ID必须为15位")
    private String customerId;


    /**
     * 归属运营商 0：缺省(硬核桃)，1：联通，2：移动，3：电信
     */
    @ApiModelProperty(value = "归属运营商 0：缺省(硬核桃)，1：联通，2：移动，3：电信")
    @NotNull(message = "账户类型编码不能为空")
    private Integer accountTypeCode;

    /**
     * chatbotAccountId(UUID)
     */
    @ApiModelProperty(value = "chatbotAccountId")
    private String chatbotAccountId;

}
