package com.citc.nce.auth.usermessage.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年7月15日09:59:35
 * @Version: 1.0
 * @Description:
 */
@Data
public class UserMsgReq {

    @NotNull
    @ApiModelProperty(value = "消息标题")
    private String msgTitle;

    @NotNull
    @ApiModelProperty(value = "消息内容")
    private String msgDetail;

    @NotNull
    @ApiModelProperty(value = "用户uuid")
    private String userUuid;

    @NotNull
    @ApiModelProperty(value = "消息来源ID")
    private Integer sourceId;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "发送时间")
    private Date postTime;

    @ApiModelProperty(value = "业务类型(1:API 2:工单)", dataType = "Integer")
    private Integer businessType;
}
