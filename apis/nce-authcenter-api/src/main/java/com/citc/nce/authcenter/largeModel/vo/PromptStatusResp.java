package com.citc.nce.authcenter.largeModel.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PromptStatusResp implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 文本识别prompt状态
     */
    @ApiModelProperty(value = "文本识别prompt状态", dataType = "Integer")
    private Integer textRecognitionStatus;
}
