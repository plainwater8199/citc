package com.citc.nce.auth.csp.readingLetter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class CspReadingLetterMsgResp  implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("错误消息")
    private String errMsg;

}
