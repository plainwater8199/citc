package com.citc.nce.auth.csp.chatbot.vo;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class ChatbotReq extends PageParam implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("chatbot名称/id")
    private String chatbotString;

    @ApiModelProperty("归属客户id")
    private String customerId;

    @ApiModelProperty("归属运营商 -1:全部 0：缺省(硬核桃)，1：联通，2：移动，3：电信")
    private Integer operatorCode;

    @ApiModelProperty("状态 -1:全部 ，30：在线，31：已下线，42：已下线（关联的CSP被下线），50：调试")
    private Integer chatbotStatus;
}
