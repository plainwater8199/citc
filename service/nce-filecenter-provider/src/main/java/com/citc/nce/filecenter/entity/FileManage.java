package com.citc.nce.filecenter.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_file_manage")
public class FileManage extends BaseDo<FileManage> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String fileName;

    private String fileFormat;

    private String fileSize;
    @ApiModelProperty(value = "文件大小")
    @TableField(exist = false)
    private Long fileLength;


    private String fileUuid;

    private Date fileUploadTime;

    private String fileUrl;

    private String fileDuration;

    private String minioFileName;

    private Date deleteTime;

    private Integer deleted;

    private Integer sameNum;

    private String thumbnailFileName;

    @TableField(value = "scenario_id")
    private String scenarioID;

    private String autoThumbnail;
}
