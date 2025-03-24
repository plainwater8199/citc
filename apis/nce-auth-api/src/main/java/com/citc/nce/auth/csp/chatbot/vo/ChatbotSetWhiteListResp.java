package com.citc.nce.auth.csp.chatbot.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class ChatbotSetWhiteListResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "手机号码", dataType = "Integer")
    private String phone;

    @ApiModelProperty(value = "排序", dataType = "Integer")
    private Integer sort;
}
