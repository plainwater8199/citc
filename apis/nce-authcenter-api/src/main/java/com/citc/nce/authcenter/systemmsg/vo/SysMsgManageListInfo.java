package com.citc.nce.authcenter.systemmsg.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.Date;

@Data
public class SysMsgManageListInfo {
    @ApiModelProperty(value = "id", dataType = "Long")
    private Long id;
    @ApiModelProperty(value = "标题", dataType = "String")
    private String title;
    @ApiModelProperty(value = "是否立即发送(0：否 1：是)", dataType = "Integer")
    private Integer isSend;
    @ApiModelProperty(value = "发送时间", dataType = "Date")
    private Date sendTime;
    @ApiModelProperty(value = "创建时间", dataType = "Date")
    private Date createTime;

}
