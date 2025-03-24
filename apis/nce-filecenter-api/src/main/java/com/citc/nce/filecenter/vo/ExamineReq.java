package com.citc.nce.filecenter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年8月26日10:38:10
 * @Version: 1.0
 * @Description: ExamineReq
 */
@Data
public class ExamineReq {
    @NotNull
    @ApiModelProperty(value = "文件uuid集合", example = "2b34ef74fe564d8d9790ac239197b4b9")
    private List<String> fileUUIDs;

    @ApiModelProperty(value = "运营商集合", example = "2b34ef74fe564d8d9790ac239197b4b9")
    private List<String> operators;

    @ApiModelProperty(value = "创建", example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String creator;
    @ApiModelProperty(value = "chatbot编号", example = "123")
    private List<String> chatbotAccounts;

    @ApiModelProperty(value = " 1-4分别是音频，文件，图片，视频")
    private Integer mediaType;
}
