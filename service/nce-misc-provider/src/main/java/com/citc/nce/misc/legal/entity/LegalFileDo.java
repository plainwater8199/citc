package com.citc.nce.misc.legal.entity;

import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsPackage: com.citc.nce.misc.leagal.entity
 * @Author: litao
 * @CreateTime: 2023-02-09  15:55
 
 * @Version: 1.0
 */
@Data
public class LegalFileDo extends BaseDo<LegalFileDo> implements Serializable {
    private static final long serialVersionUID = 1L;

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

    @ApiModelProperty("是否删除(0:否 1:是)")
    private Integer deleted;

    @ApiModelProperty("删除时间")
    private Long deletedTime;

    @ApiModelProperty("版本号")
    private Integer version;
}
