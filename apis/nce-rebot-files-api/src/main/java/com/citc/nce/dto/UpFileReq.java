package com.citc.nce.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: UpFileDto
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UpFileReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = 508438515683059491L;

    @NotNull
    @ApiModelProperty(value = "文件名称",example = "a.pdf")
    private String fileName;

    @NotNull
    @ApiModelProperty(value = "文件urlId",example = "2b34ef74fe564d8d9790ac239197b4b9")
    private String fileUrlId;

    @NotNull
    @ApiModelProperty(value = "文件大小",example = "1.2M")
    private String fileSize;

    @ApiModelProperty("用户uuid")
    private String creator;

    private String thumbnailTid;

    // 供应商tag
    private String supplierTag;

    // 文件状态
    private Integer fileStatus;
}
