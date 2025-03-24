package com.citc.nce.auth.unicomAndTelecom.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ChatbotUploadResp {

    @ApiModelProperty(value = "本地文件路径")
    private String localUrl;

    @ApiModelProperty(value = "运营商文件路径")
    private String operatorUrl;
}
