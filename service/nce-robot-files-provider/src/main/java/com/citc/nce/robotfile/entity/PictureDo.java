package com.citc.nce.robotfile.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
 * @Description: PictureDo
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_picture")
public class PictureDo extends BaseDo<PictureDo> implements Serializable {


    private static final long serialVersionUID = -5607200919535690833L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String pictureName;

    private Long groupId;

    private String pictureUrlId;

    private Date pictureUploadTime;

    private Integer deleted;

    private Date deleteTime;

    private Integer used;

    private String pictureFormat;

    private String pictureSize;

    private String thumbnailTid;

    @TableField(exist = false)
    private Long oldId;
}
