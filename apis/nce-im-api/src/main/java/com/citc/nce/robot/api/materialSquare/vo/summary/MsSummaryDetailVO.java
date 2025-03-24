package com.citc.nce.robot.api.materialSquare.vo.summary;

import com.citc.nce.common.xss.core.XssCleanIgnore;
import com.citc.nce.robot.api.materialSquare.entity.MsSummary;
import com.citc.nce.robot.api.tempStore.enums.PayStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
public class MsSummaryDetailVO extends MsSummary {
    @ApiModelProperty("作品介绍")
    @XssCleanIgnore
    private String introduce;
    @ApiModelProperty("模板内容json")
    private String templateJson;
    @ApiModelProperty("是否点赞")
    private Boolean ifLike = false;
    @ApiModelProperty("购买量")
    private long payCount = 0;
    @ApiModelProperty("支付状态")
    private PayStatus payStatus = null;
    @ApiModelProperty("活动方案id")
    private Long msActivityContentId = -1L;
    @ApiModelProperty("折扣")
    private Double discountRate;
    @ApiModelProperty("创建者名称")
    private String creatorName;
    @ApiModelProperty("企业名称")
    private String enterpriseName;
    @ApiModelProperty("企业名称-唯一")
    private String enterpriseAccountName;
    @ApiModelProperty("活动价格")
    private BigDecimal activityPrice;
    @ApiModelProperty("H5活动图")
    private String formCover;
}
