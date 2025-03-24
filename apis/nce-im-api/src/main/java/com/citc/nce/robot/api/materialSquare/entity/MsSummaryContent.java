package com.citc.nce.robot.api.materialSquare.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 素材广场，发布汇总，作品内容快照
 * </p>
 *
 * @author bydud
 * @since 2024-05-31 09:05:58
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ms_summary_content")
@ApiModel(value = "MsSummaryContent对象", description = "素材广场，发布汇总，作品内容快照")
public class MsSummaryContent implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "mssc_id", type = IdType.ASSIGN_ID)
    private Long msscId;

    @TableField("mss_id")
    private Long mssId;

    @ApiModelProperty("更新版本")
    @TableField("content_version")
    private Integer contentVersion;

    @ApiModelProperty("素材类型")
    @TableField("ms_type")
    private MsType msType;

    @ApiModelProperty("作品具体内容")
    @TableField("ms_json")
    private String msJson;

}
