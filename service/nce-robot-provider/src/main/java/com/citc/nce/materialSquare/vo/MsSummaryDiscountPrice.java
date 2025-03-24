package com.citc.nce.materialSquare.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.citc.nce.materialSquare.PromotionType;
import com.citc.nce.materialSquare.entity.MsManageActivityContent;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author bydud
 * @since 2024/6/21 15:54
 */
@Data
public class MsSummaryDiscountPrice {

    @ApiModelProperty("素材ms_summary表id")
    private Long mssId;
    @ApiModelProperty("素材原始价格")
    private BigDecimal originalPrice;
    @ApiModelProperty("折扣价格（空或负数无效）")
    private BigDecimal discountPrice;

    @ApiModelProperty("活动创建时间")
    private Date msActivityCreateTime;
    @ApiModelProperty("活动创建人id 管理员id")
    private String msActivityCreator;
    @ApiModelProperty("关联表id")
    private Long msActivityLiId;
    @ApiModelProperty("活动方案id")
    private Long msActivityContentId;
    @ApiModelProperty("活动类型")
    @TableField("promotion_type")
    private PromotionType promotionType;
    @ApiModelProperty("折扣")
    @TableField("discount_rate")
    private Double discountRate;

    @ApiModelProperty("活动方案")
    private MsManageActivityContent activityContent;
}
