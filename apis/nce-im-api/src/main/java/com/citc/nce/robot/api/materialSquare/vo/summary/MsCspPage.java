package com.citc.nce.robot.api.materialSquare.vo.summary;

import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.robot.api.materialSquare.emums.MsAuditStatus;
import com.citc.nce.robot.api.materialSquare.emums.MsTemplateStatus;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author bydud
 * @since 2024/6/20 15:13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MsCspPage extends PageParam {
    @ApiModelProperty("类型")
    private MsType msType;
    @ApiModelProperty("商品状态")
    private MsAuditStatus auditStatus;
    @ApiModelProperty("模板状态")
    private MsTemplateStatus templateStatus;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("排序字段 默认createTime,根据浏览量排序:viewCount,根据点赞数排序:likesCount")
    private String sortField = "createTime";
    @ApiModelProperty("排序顺序 默认desc,正序:asc")
    private String sortOrder = "desc";
}
