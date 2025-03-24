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
 * @Description: AudioDo
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("tb_audio")
@Accessors(chain = true)
public class AudioDo extends BaseDo<AudioDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    private String audioName;

    private String audioDuration;

    private String audioSize;

    private String audioFormat;

    private String audioUrlId;

    private Date audioUploadTime;

    private Integer deleted;

    private Date deleteTime;

    private Integer used;

    //记录原始id
    @TableField(exist = false)
    private Long oldId;
}
