package com.citc.nce.filecenter.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.citc.nce.filecenter.enums.UploadFileType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class UploadReq {

    @NotNull
    @ApiModelProperty(value = "场景ID",example = "Sx5XDHVos7：h5=H5，富文本=richText")
    private String sceneId;

    @NotNull
    @JSONField(serialize = false)
    @ApiModelProperty(value = "文件",example = "aa.mp4")
    private MultipartFile file;

    @JSONField(serialize = false)
    @ApiModelProperty(value = "视频缩略图",example = "aa.png")
    private MultipartFile thumbnail;

    @ApiModelProperty(value = "账号集合字符串",example = "aa.mp4")
    private String list;

    @ApiModelProperty(value = "用户信息",example = "aaaaa")
    private String creator;

    @ApiModelProperty(value = "应用场景id",example = "")
    private String scenarioID;

}
