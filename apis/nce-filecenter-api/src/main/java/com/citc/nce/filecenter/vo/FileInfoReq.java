package com.citc.nce.filecenter.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class FileInfoReq {
    @NotNull
    @ApiModelProperty(value = "文件uuid",required = true)
    private String uuid;
    @ApiModelProperty(value = "文件场景应用ID")
    private String scenarioID;
}
