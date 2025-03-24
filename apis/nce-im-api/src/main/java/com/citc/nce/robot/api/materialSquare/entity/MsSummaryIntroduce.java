package com.citc.nce.robot.api.materialSquare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 素材广场，发布汇总，作品介绍
 * </p>
 *
 * @author bydud
 * @since 2024-05-31 09:05:58
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ms_summary_introduce")
@ApiModel(value = "MsSummaryIntroduce对象", description = "素材广场，发布汇总，作品介绍")
public class MsSummaryIntroduce implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "mssi_id", type = IdType.ASSIGN_ID)
    private Long mssiId;

    @TableField("mss_id")
    private Long mssId;

    @ApiModelProperty("介绍内容版本")
    @TableField("introduce_version")
    private Integer introduceVersion;

    @ApiModelProperty("作品介绍")
    @TableField("introduce")
    private String introduce;


}
