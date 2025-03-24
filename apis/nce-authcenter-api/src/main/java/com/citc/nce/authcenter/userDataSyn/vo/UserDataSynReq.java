package com.citc.nce.authcenter.userDataSyn.vo;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class UserDataSynReq {
    @JSONField(serialize = false)
    @NotNull
    @ApiModelProperty(value = "文件",example = "aa.mp4")
    private MultipartFile file;
}
