package com.citc.nce.authcenter.legalaffairs.vo.resp;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @BelongsPackage: com.citc.nce.misc.legal.resp
 * @Author: hhluop
 * @CreateTime: 2023-03-07  16:41
 * @Description: 法务文件更新记录返回vo
 * @Version: 1.0
 */
@Data
public class LegalRecordResp {

    @ApiModelProperty("处理时间")
    @JsonFormat(timezone ="GMT+8",pattern = DatePattern.NORM_DATETIME_PATTERN)
    private Date operateTime;
    @ApiModelProperty("处理内容")
    private String processingContent;
    @ApiModelProperty("处理人员")
    private String processingUserName;
    @ApiModelProperty("备注")
    private String remark;
}
