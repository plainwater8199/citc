package com.citc.nce.filecenter.tempStore;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Accessors(chain = true)
public class SaveMaterialReq {
    @ApiModelProperty(value = "关联5G消息账号")
    @NotEmpty(message = "机器人账号不能为空")
    private String[] chatbotAccount;

    @ApiModelProperty(value = "用户")
    @NotBlank(message = "用户不能为空")
    private String userId;

    List<ResourcesImg> pictureList;
    List<ResourcesVideo> videoList;
    List<ResourcesAudio> audioList;
}
