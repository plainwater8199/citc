package com.citc.nce.materialSquare.vo.activity.resp;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.materialSquare.PromotionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 素材广场_后台管理_活动方案
 * </p>
 *
 * @author bydud
 * @since 2024-05-14 02:05:31
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "MsManageActivityContent对象", description = "素材广场_后台管理_活动方案")
public class MsManageActivityContentResp implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("活动方案id")
    private Long msActivityContentId;

    @ApiModelProperty("活动名称")
    private String name;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("活动类型")
    private PromotionType promotionType;

    @ApiModelProperty("折扣")
    private Double discountRate;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private String createTime;

    @ApiModelProperty("活动状态")
    private String status;

    @ApiModelProperty("作品数量")
    private Integer msCount;


}
