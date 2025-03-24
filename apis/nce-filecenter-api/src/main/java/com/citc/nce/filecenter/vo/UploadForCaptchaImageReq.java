package com.citc.nce.filecenter.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.validation.constraints.NotNull;


@Data
public class UploadForCaptchaImageReq {

    @NotNull
    @JSONField(serialize = false)
    @ApiModelProperty(value = "文件",example = "aa.mp4")
    private MultipartFile file;


}
