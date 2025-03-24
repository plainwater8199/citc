package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
public class QuerySupplierChatbotReq {
    private static final Boolean IS_NOT_EXPORT = true;

    @NotNull(message = "pageNo不能为空")
    @ApiModelProperty(value = "pageNo", dataType = "Integer", required = true)
    private Integer pageNo;

    @NotNull(message = "pageSize不能为空")
    @ApiModelProperty(value = "pageSize", dataType = "Integer", required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "通道(0 all ,1蜂动电信 2蜂动移动)", dataType = "Integer", required = false)
    private Integer channelType;

    @ApiModelProperty(value = "Chatbot状态 待处理：0;  上线：1;  已注销：2 ;   下线：3;   已驳回：4", dataType = "Integer", required = false)
    private Integer chatbotStatus;

    @ApiModelProperty(value = "chatbotAccount或者名字", dataType = "String", required = false)
    private String idOrName;

    @ApiModelProperty(value = "归属用户名", dataType = "String", required = false)
    private String customerName;

}
