package com.citc.nce.robot.api.materialSquare.vo.summary;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.citc.nce.common.bean.CspNameBase;
import com.citc.nce.robot.api.materialSquare.emums.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author bydud
 * @since 2024/6/19 11:59
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MsPageResult extends CspNameBase {

    @ApiModelProperty("表主键")
    private Long mssId;

    @ApiModelProperty("编号")
    private String msNum;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("描述")
    private String msDesc;

    @ApiModelProperty("类型")
    private MsType msType;

    @ApiModelProperty("原价")
    private BigDecimal originalPrice;

    @ApiModelProperty("折扣价格（空或负数无效）")
    private BigDecimal discountPrice;

    @ApiModelProperty("商品状态")
    private MsAuditStatus auditStatus;

    @ApiModelProperty("失败原因")
    private String auditFailResult;

    @ApiModelProperty("模板状态")
    private MsTemplateStatus templateStatus;

    @ApiModelProperty("付费类型 free paid")
    private MsPayType payType;

    @ApiModelProperty("作品库状态")
    private MsWorksLibraryStatus worksLibraryStatus;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(locale = "zh", pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT)
    @JsonFormat(locale = "zh", pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    @ApiModelProperty("cspId")
    private String cspId;

    @ApiModelProperty("点赞数量")
    private BigDecimal likesCount;

    @ApiModelProperty("浏览数量")
    private BigDecimal viewCount;

    @ApiModelProperty("封面类型 custom thumbnail")
    private MsCoverType coverType;

    @ApiModelProperty("封面图")
    private String coverFile;

    @ApiModelProperty("来源")
    private MsSource msSource;

    @ApiModelProperty("更新时间")
    @JsonFormat(locale = "zh", pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishTime;

    @ApiModelProperty("h5 ID for h5" )
    private Long h5Id;

    @ApiModelProperty("封面图")
    private String autoThumbnail;

}
