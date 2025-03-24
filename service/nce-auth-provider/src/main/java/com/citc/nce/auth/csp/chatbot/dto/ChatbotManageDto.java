package com.citc.nce.auth.csp.chatbot.dto;

import com.citc.nce.common.core.pojo.PageParam;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**

 *
 * @Author zy.qiu
 * @CreatedTime 2023/1/19 15:43
 */
@Data
public class ChatbotManageDto extends PageParam {

    @ApiModelProperty("chatbot名称/id")
    private String chatbotString;

    @ApiModelProperty("归属客户id")
    private String customerId;

    @ApiModelProperty("归属运营商 -1:全部 0：缺省(硬核桃)，1：联通，2：移动，3：电信")
    private Integer operatorCode;

    @ApiModelProperty("状态 -1:全部 ，30：在线，31：已下线，42：已下线（关联的CSP被下线），50：调试")
    private Integer chatbotStatus;

    private Integer currentPage;

    private String cspId;

    private String operatorName;
}
