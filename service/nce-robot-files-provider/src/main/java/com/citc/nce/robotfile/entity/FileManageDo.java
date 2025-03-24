package com.citc.nce.robotfile.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_file_manage")
public class FileManageDo extends BaseDo<FileManageDo> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String fileName;

    private String fileFormat;

    private String fileSize;

    private String fileUuid;

    private Date fileUploadTime;

    private String fileUrl;

    private String fileDuration;

    private String minioFileName;

    private Date deleteTime;

    private Integer deleted;

    private Integer sameNum;

    private String thumbnailFileName;

}
