package com.citc.nce.materialSquare.vo.activity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.citc.nce.common.bean.CspNameBase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author bydud
 * @since 2024/5/15 9:40
 */

@Data
@ApiModel(description = "活动作品列表")
public class LiPageResult extends CspNameBase {
    @ApiModelProperty("关联表id")
    private Long msActivityLiId;
    @ApiModelProperty("id编号")
    private String IdNO;
    @ApiModelProperty("作品名称")
    private String name;
    @ApiModelProperty("来源平台")
    private String from;
    @ApiModelProperty("素材原始价格")
    private BigDecimal originalPrice;
    @ApiModelProperty("折扣价格（空或负数无效）")
    private BigDecimal discountPrice;
    @ApiModelProperty("封面图")
    private String coverFile;
    @ApiModelProperty("封面图")
    private String coverFileName;

}
