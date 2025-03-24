package com.citc.nce.misc.record.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("processing_record")
public class ProcessingRecordDo extends BaseDo<ProcessingRecordDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("业务主键id")
    private String businessId;

    @ApiModelProperty("业务类型")
    private Integer businessType;

    @ApiModelProperty("操作时间")
    private Date operateTime;

    @ApiModelProperty("处理内容")
    private String processingContent;

    @ApiModelProperty("处理人员id")
    private String processingUserId;

    @ApiModelProperty("备注")
    private String remark;
}
