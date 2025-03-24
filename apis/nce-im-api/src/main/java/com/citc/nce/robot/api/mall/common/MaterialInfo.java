package com.citc.nce.robot.api.mall.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>TODO</p>
 *
 * @Author zy.qiu
 * @CreatedTime 2023/11/25 14:32
 */
@Data
public class MaterialInfo {
    @ApiModelProperty(value = "文件urlId", example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String urlId;

    @ApiModelProperty(value = "文件格式", example = "mp3")
    private String fileFormat;

    @ApiModelProperty(value = "文件名称", example = "aa.mp3")
    private String fileName;

    @ApiModelProperty(value = "文件大小", example = "1.2M")
    private String fileSize;

    @ApiModelProperty(value = "文件时长", example = "1分20秒")
    private String fileDuration;

    @ApiModelProperty(value = "缩略图id", example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String thumbnailTid;
}
