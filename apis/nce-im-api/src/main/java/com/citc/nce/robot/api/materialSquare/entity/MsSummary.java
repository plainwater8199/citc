package com.citc.nce.robot.api.materialSquare.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.citc.nce.robot.api.materialSquare.emums.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 素材广场，发布汇总
 * </p>
 *
 * @author bydud
 * @since 2024-05-31 09:05:58
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ms_summary")
@ApiModel(value = "MsSummary对象", description = "素材广场，发布汇总")
public class MsSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("素材id")
    @TableId(value = "mss_id", type = IdType.ASSIGN_ID)
    private Long mssId;

    @ApiModelProperty("cspId")
    @TableField("csp_id")
    private String cspId;

    @ApiModelProperty("素材类型")
    @TableField("ms_type")
    private MsType msType;

    @ApiModelProperty("素材的id 表主键")
    @TableField("ms_id")
    private String msId;

    @ApiModelProperty("作品名称")
    @TableField("name")
    private String name;

    @ApiModelProperty("素材编号")
    @TableField("ms_num")
    private String msNum;

    @ApiModelProperty("描述")
    @TableField("ms_desc")
    private String msDesc;

    @ApiModelProperty("标签 , 隔开")
    @TableField("ms_tag")
    private String msTag;

    @ApiModelProperty("封面类型 custom thumbnail")
    @TableField("cover_type")
    private MsCoverType coverType;

    @ApiModelProperty("封面图")
    @TableField("cover_file")
    private String coverFile;

    @ApiModelProperty("封面图")
    @TableField("cover_file_name")
    private String coverFileName;

    @ApiModelProperty("是否删除")
    @TableField("deleted")
    @TableLogic
    private Integer deleted;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @ApiModelProperty("模板更新状态 0:模板未更新 1:模板已更新")
    @TableField("template_status")
    private MsTemplateStatus templateStatus;


    @ApiModelProperty("模板审核状态")
    @TableField("audit_status")
    private MsAuditStatus auditStatus;

    @ApiModelProperty("失败原因")
    @TableField("audit_fail_result")
    private String auditFailResult;

    @ApiModelProperty("作品库状态")
    @TableField("works_library_status")
    private MsWorksLibraryStatus worksLibraryStatus;

    @ApiModelProperty("付费类型 free paid")
    @TableField("pay_type")
    private MsPayType payType;

    @ApiModelProperty("原价")
    @TableField("original_price")
    private BigDecimal originalPrice;

    @ApiModelProperty("折扣价")
    @TableField("discount_price")
    private BigDecimal discountPrice;

    @ApiModelProperty("作品内容（模板）版本")
    @TableField("content_version")
    private Integer contentVersion;

    @ApiModelProperty("作品介绍版本")
    @TableField("introduce_version")
    private Integer introduceVersion;

    @ApiModelProperty("点赞数量")
    @TableField("likes_count")
    private BigDecimal likesCount;

    @ApiModelProperty("浏览数量")
    @TableField("view_count")
    private BigDecimal viewCount;

    @ApiModelProperty("标签")
    @TableField(exist = false)
    private List<String> tags;

    @ApiModelProperty("更新者")
    @TableField("updater")
    private String updater;

    @ApiModelProperty("更新时间")
    @TableField("update_time")
    private Date updateTime;

    @ApiModelProperty("更新时间")
    @TableField("publish_time")
    private Date publishTime;

    @ApiModelProperty("首次上架时间")
    @TableField("first_putaway_time")
    private Date firstPutawayTime;

    @ApiModelProperty("最近上架时间")
    @TableField("last_putaway_time")
    private Date lastPutawayTime;

    public List<String> getTags() {
        if(StringUtils.hasLength(this.msTag)){
            return Arrays.asList(this.msTag.split(","));
        }else{
            return null;
        }
    }
}
