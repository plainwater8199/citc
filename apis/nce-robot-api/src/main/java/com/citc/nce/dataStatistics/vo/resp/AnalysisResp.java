package com.citc.nce.dataStatistics.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class AnalysisResp {

    /**
     * 计划个数
     */
    private Integer planNum = 0;

    @ApiModelProperty(value = "发送数量")
    private Long sendAmount = 0L;

    @ApiModelProperty(value = "天")
    private String sendTimeDay;

    @ApiModelProperty(value = "小时")
    private String sendTimeHour;

    @ApiModelProperty(value = "小时")
    private String sendTimeWeek;

    @ApiModelProperty(value = "成功数量")
    private Long successNum = 0L;

    @ApiModelProperty(value = "未知数量")
    private Long unknowNum = 0L;

    @ApiModelProperty(value = "失败数量")
    private Long failedNum = 0L;

    @ApiModelProperty(value = "已读数量")
    private Long readNum = 0L;

    @ApiModelProperty(value = "机器人发送数量")
    private Long robotSendNum;

    /**
     * 计划执行量
     */
    @ApiModelProperty(value = "计划执行量")
    private Integer executeNum = 0;

    /**
     * 群发次数
     */
    @ApiModelProperty(value = "群发次数")
    private Integer massNum = 0;

    @ApiModelProperty(value = "运营商")
    private String operator;

    @ApiModelProperty(value = "计划id集合")
    private String planIds;

    @ApiModelProperty(value = "点击次数")
    private Long clickAmount = 0L;

    @ApiModelProperty(value = "按钮名称")
    private String btnName;

    @ApiModelProperty(value = "成功率")
    private String successRate;

    @ApiModelProperty(value = "浏览率")
    private String browsingRate;

    @ApiModelProperty(value = "其它按钮集合")
    private List<AnalysisResp> others;

    private Long mobileSendAmount = 0L;

    private Long unicomSendAmount = 0L ;

    private Long telecomSendAmount = 0L;

    private Long walnutSendAmount = 0L;

    private Long mediaSendAmount = 0L;

    private Long mediaSuccessSendAmount = 0L;

    private Long shortMsgSendAmount = 0L;

    private Long shortMsgSuccessSendAmount = 0L;

    private Long msgSuccessSendAmount = 0L;

    private Long msgSendAmount = 0L;

}
