package com.citc.nce.misc.record.req;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ProcessingRecordReq {
    @ApiModelProperty("业务主键id")
    private String businessId;

    @ApiModelProperty("业务类型")
    private Integer businessType;

    @ApiModelProperty("处理内容")
    private String processingContent;

    @ApiModelProperty("处理人员id")
    private String processingUserId;

    @ApiModelProperty("备注")
    private String remark;
}
