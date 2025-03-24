package com.citc.nce.filecenter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @Author: lidingyi
 * @Contact: dylicr
 * @Date: 2022/6/27 16:51
 * @Version: 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class DownloadReq {

    @NotNull
    @ApiModelProperty(value = "文件urlId",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String fileUUID;
    @ApiModelProperty(value = "range")
    private String range;
}
