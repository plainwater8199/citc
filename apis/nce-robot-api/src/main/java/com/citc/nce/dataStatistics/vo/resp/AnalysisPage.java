package com.citc.nce.dataStatistics.vo.resp;

import com.citc.nce.common.core.pojo.PageResult;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel
public class AnalysisPage {

    @ApiModelProperty(value = "总条数（不分页）")
    private List<AnalysisResp> totalList;

    @ApiModelProperty(value = "分页结果")
    private PageResult<AnalysisResp> pageResult;

    @ApiModelProperty(value = "发送数量")
    private Long sendAmount;

    @ApiModelProperty(value = "成功数量")
    private Long successNum;

    @ApiModelProperty(value = "未知数量")
    private Long unknowNum;

    @ApiModelProperty(value = "失败数量")
    private Long failedNum;

    @ApiModelProperty(value = "已读数量")
    private Long readNum;

    /**
     * 计划执行量
     */
    @ApiModelProperty(value = "计划执行量")
    private Integer executeNum;

    /**
     * 群发次数
     */
    @ApiModelProperty(value = "群发次数")
    private Integer massNum;

    @ApiModelProperty(value = "计划个数")
    private Integer planNum;

    @ApiModelProperty(value = "点击次数")
    private Long clickAmount;

    @ApiModelProperty(value = "成功占比")
    private String successPercentage;

    @ApiModelProperty(value = "已阅占比")
    private String readPercentage;

    @ApiModelProperty(value = "点击占比")
    private String clickPercentage;

    private Long mediaSendAmount = 0L;
    private Long shortMsgSendAmount = 0L;

    private Long msgSendAmount = 0L;

}
