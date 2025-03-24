package com.citc.nce.filecenter.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.File;
import java.io.Serializable;

@Data
public class FileInfo implements Serializable {
    @ApiModelProperty(value = "文件名称")
    private String fileName;
    @ApiModelProperty(value = "文件类型")
    private String fileFormat;
    @ApiModelProperty("文件大小、字节数")
    private Long fileSize;
    @ApiModelProperty(value = "文件")
    @JsonIgnore
    private transient File file;
    private String creator;
}
