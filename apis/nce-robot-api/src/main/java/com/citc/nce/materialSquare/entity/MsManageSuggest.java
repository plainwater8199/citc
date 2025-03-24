package com.citc.nce.materialSquare.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 素材广场_后台管理_首页推荐	
 * </p>
 *
 * @author bydud
 * @since 2024-05-15 10:05:47
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("ms_manage_suggest")
@ApiModel(value = "MsManageSuggest对象", description = "素材广场_后台管理_首页推荐	")
public class MsManageSuggest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("推荐id")
    @TableId(value = "ms_suggest_id", type = IdType.ASSIGN_ID)
    private Long msSuggestId;

    @ApiModelProperty("排序（指定的为0，不参与排序计算）")
    @TableField("order_num")
    private Integer orderNum;

    @ApiModelProperty("排序方式：0-正序，1-乱序")
    @TableField("order_type")
    private Integer orderType;

    @ApiModelProperty("作品的id")
    @TableField("mss_id")
    private Long mssId;

    @ApiModelProperty("作品id")
    @TableField("ms_id_field")
    private Integer msIdField;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @TableField(value = "creator", fill = FieldFill.INSERT)
    private String creator;

    @TableField(value = "updater", fill = FieldFill.INSERT_UPDATE)
    private String updater;

    @TableField("deleted")
    @TableLogic
    private Integer deleted;


}
