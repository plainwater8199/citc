package com.citc.nce.auth.csp.chatbot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class ChatbotActiveOnReq implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "chatbotAccountId", dataType = "String", required = true)
    @NotNull
    private String chatbotAccountId;

    @ApiModelProperty(value = "chatbotId", dataType = "String", required = true)
    @NotNull
    private String chatbotId;

    @ApiModelProperty(value = "测试报告url", required = true)
    @NotNull
    private String shelvesFileUrl;

    @ApiModelProperty(value = "上架申请说明")
    @Length(max = 50, message = "上架申请说明长度超过限制(最大50位)")
    private String shelvesDesc;
}
