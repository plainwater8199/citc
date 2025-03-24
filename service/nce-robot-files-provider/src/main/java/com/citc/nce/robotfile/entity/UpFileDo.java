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

/**
 * @Author: wenliuch
 * @Contact: wenliuch
 * @Date: 2022年6月29日16:29:48
 * @Version: 1.0
 * @Description: UpFileDo
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_file")
public class UpFileDo extends BaseDo<UpFileDo> implements Serializable {

    private static final long serialVersionUID = 5457395347861119087L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String fileName;

    private String fileFormat;

    private String fileUrlId;

    private String fileSize;

    private Date fileUploadTime;

    private Date deleteTime;

    private Integer deleted;

    private Integer used;
}
