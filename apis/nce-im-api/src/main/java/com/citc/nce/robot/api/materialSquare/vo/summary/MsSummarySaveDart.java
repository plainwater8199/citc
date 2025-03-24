package com.citc.nce.robot.api.materialSquare.vo.summary;

import com.baomidou.mybatisplus.annotation.TableField;
import com.citc.nce.robot.api.materialSquare.emums.MsAuditStatus;
import com.citc.nce.robot.api.materialSquare.emums.MsCoverType;
import com.citc.nce.robot.api.materialSquare.emums.MsPayType;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 素材广场发布草稿
 *
 * @author bydud
 * @since 2024/5/31 10:18
 */

@Data
@Accessors(chain = true)
public class MsSummarySaveDart implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("作品名称")
    @NotBlank(message = "素材名称不能为空")
    @Length(max = 25, message = "素材名称不能超过25字符")
    private String name;

    @ApiModelProperty("素材类型")
    @NotNull(message = "素材类型不能为空")
    private MsType msType;

    @ApiModelProperty("素材的id 表主键")
    @NotBlank(message = "素材的id不能为空")
    private String msId;

    @ApiModelProperty("作品概述")
    @Length(max = 500, message = "作品概述不能超过500字符")
    private String msDesc;

    @ApiModelProperty("标签 , 隔开")
    @Size(max = 5, message = "标签不能超过5个")
    private List<String> msTag;

    @ApiModelProperty("付费类型 free paid")
    @NotNull(message = "付费类型不能为空")
    private MsPayType payType;

    @ApiModelProperty("原价")
    @DecimalMin(value = "0.0", inclusive = true, message = "价格最低为必须大于0.0")
    @DecimalMax(value = "100000000.0", inclusive = true, message = "价格最低为必须小于等于100000000.0")
    private BigDecimal originalPrice;

    @ApiModelProperty("封面类型 custom thumbnail")
    @NotNull(message = "封面类型不能为空")
    private MsCoverType coverType;

    @ApiModelProperty("封面图")
    @NotBlank(message = "封面图不能为空")
    private String coverFile;

    @ApiModelProperty("封面图")
    @NotBlank(message = "封面图名称不能为空")
    private String coverFileName;

    @ApiModelProperty("作品介绍")
    private String introduce;

    @ApiModelProperty("cspId")
    private String cspId;

    @ApiModelProperty("模板审核状态")
    @NotNull(message = "模板审核状态不能为空")
    private MsAuditStatus auditStatus;
}
