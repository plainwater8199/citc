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
 * @Description: VideoDo
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_video")
public class VideoDo extends BaseDo<VideoDo>  implements Serializable {

    private static final long serialVersionUID = 3230826309904194175L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String videoName;

    private String videoDuration;

    private String videoSize;

    private String videoFormat;

    private Date videoUploadTime;

    private String videoUrlId;

    private String coverUrlIds;

    private String mainCoverId;

    private Date deleteTime;

    private Integer deleted;

    private Integer used;

    private String mainCoverBackId;

    private String cropObj;

    private String thumbnailTid;

    //记录原始id
    @TableField(exist = false)
    private Long oldId;
}
