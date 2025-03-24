package com.citc.nce.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年8月27日14:21:13
 * @Version: 1.0
 * @Description: FileReq
 */
@Data
public class FileReq {

    @NotNull
    @ApiModelProperty(value = "素材UUID",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String fileUuid;
}
