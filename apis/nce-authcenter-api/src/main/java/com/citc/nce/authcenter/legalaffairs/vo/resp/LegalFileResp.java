package com.citc.nce.authcenter.legalaffairs.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @BelongsPackage: com.citc.nce.misc.legal.resp
 * @Author: litao
 * @CreateTime: 2023-02-09  16:41

 * @Version: 1.0
 */
@Data
public class LegalFileResp {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("上级id")
    private Long parentId;

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

    @ApiModelProperty("创建者")
    private String creator;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新者")
    private String updater;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("是否删除(0:否 1:是)")
    private Integer deleted;

    @ApiModelProperty("删除时间")
    private Long deletedTime;
}
