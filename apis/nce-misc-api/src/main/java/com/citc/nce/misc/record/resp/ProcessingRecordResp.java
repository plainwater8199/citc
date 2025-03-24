package com.citc.nce.misc.record.resp;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;


@Data
public class ProcessingRecordResp {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("业务主键id")
    private String businessId;

    @ApiModelProperty(value = "业务类型")
    private Integer businessType;

    @ApiModelProperty("业务类型列表")
    private List<Integer> businessTypeList;

    @ApiModelProperty("操作时间")
    private Date operateTime;

    @ApiModelProperty("处理内容")
    private String processingContent;

    @ApiModelProperty("处理人员id")
    private String processingUserId;

    @ApiModelProperty("处理人员名称")
    private String processingUserName;

    @ApiModelProperty("备注")
    private String remark;
}
