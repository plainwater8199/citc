package com.citc.nce.auth.usermessage.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年7月15日09:59:35
 * @Version: 1.0
 * @Description:
 */
@Data
public class UserMsgResp {

    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;

    @ApiModelProperty(value = "msgId", dataType = "String")
    private String msgId;

    @ApiModelProperty(value = "标题", dataType = "String")
    private String msgTitle;

    @ApiModelProperty(value = "发送时间", dataType = "Date")
    private Date postTime;

    @ApiModelProperty(value = "消息类型", dataType = "String")
    private Integer msgType;
}
