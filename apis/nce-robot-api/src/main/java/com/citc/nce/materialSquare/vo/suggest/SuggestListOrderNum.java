package com.citc.nce.materialSquare.vo.suggest;

import com.baomidou.mybatisplus.annotation.TableField;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bydud
 * @since 2024/5/15 16:37
 */
@Data
public class SuggestListOrderNum {

    @ApiModelProperty("推荐id")
    private Long msSuggestId;

    @ApiModelProperty("排序（置顶的为0，不参与排序计算）")
    private Integer orderNum;

    @ApiModelProperty("作品库id")
    private Long mssId;

    @ApiModelProperty("作品类型id")
    private Integer msIdField;

    @ApiModelProperty("作品名称")
    private String mssName;

    @ApiModelProperty("作品图片")
    private String mssCoverFile;

    @ApiModelProperty("作品库id")
    private MsType suggestType;

    @ApiModelProperty("h5 ID for h5" )
    private Long h5Id;
}
