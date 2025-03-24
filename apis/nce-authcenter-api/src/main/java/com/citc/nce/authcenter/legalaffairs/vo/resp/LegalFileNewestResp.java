package com.citc.nce.authcenter.legalaffairs.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsPackage: com.citc.nce.misc.legal.resp
 * @Author: hhluop
 * @CreateTime: 2023-03-01  16:41
 * @Description:
 * @Version: 1.0
 */
@Data
public class LegalFileNewestResp {

    @ApiModelProperty("文件id")
    private Long id;

    @ApiModelProperty("文件名称")
    private String fileName;

    @ApiModelProperty("文件内容")
    private String fileContent;

    @ApiModelProperty("文件版本")
    private Integer fileVersion;

    @ApiModelProperty("是否最新版本（0：否 1：是）")
    private Integer isLatest;

    @ApiModelProperty("是否需要用户确认（0：否 1：是）")
    private Integer isConfirm;

    @ApiModelProperty("版本号")
    private Integer version;
}
