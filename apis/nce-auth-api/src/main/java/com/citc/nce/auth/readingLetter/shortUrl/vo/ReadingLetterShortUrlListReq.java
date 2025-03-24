package com.citc.nce.auth.readingLetter.shortUrl.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zjy
 */
@Data
public class ReadingLetterShortUrlListReq {
    @ApiModelProperty("模板名称，模糊查找")
    private String templateName;

    @ApiModelProperty("运营商编码 0：缺省(硬核桃)，1：联通，2：移动，3：电信")
    private Integer operatorCode;

    @ApiModelProperty("审核状态 0全部 1审核通过 2审核被拒")
    private Integer auditStatus;

    @ApiModelProperty("任务状态 0全部 1进行中、2已过期、3已结束")
    private Integer taskStatus;

    @NotNull
    private Long pageNo;

    @NotNull
    private Long pageSize;

}
