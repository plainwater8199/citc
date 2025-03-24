package com.citc.nce.auth.unicomAndTelecom.req;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class UploadReq {

    @JSONField(serialize = false)
    @NotNull
    @ApiModelProperty(value = "文件", example = "aa.mp4")
    private MultipartFile file;

    @NotNull
    @ApiModelProperty(value = "场景ID",example = "Sx5XDHVos7")
    private String sceneId;

    @ApiModelProperty(value = "上传资料类型", example = "aa.mp4")
    private Integer uploadType;

    @ApiModelProperty(value = "运营商类型", example = "aa.mp4")
    private Integer operatorCode;

    private String fileUrl;
}
