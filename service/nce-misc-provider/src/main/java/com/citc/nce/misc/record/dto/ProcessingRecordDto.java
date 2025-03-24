package com.citc.nce.misc.record.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ProcessingRecordDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("业务主键id")
    private String businessId;

    @ApiModelProperty("业务类型")
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
