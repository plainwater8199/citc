package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.sql.Date;


/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/10/30 19:14
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
@ApiModel
public class ConversationalInteractionDaysResp {

    @ApiModelProperty(value = "发送量", dataType = "Long")
    private Long sendNum;

    @ApiModelProperty(value = "上行量", dataType = "Long")
    private Long upsideNum;

    @ApiModelProperty(value = "会话量", dataType = "Long")
    private Long sessionNum;

    @ApiModelProperty(value = "有效会话量", dataType = "Long")
    private Long effectiveSessionNum;

    @ApiModelProperty(value = "时间(每天)", dataType = "Date")
    private Date days;
}
