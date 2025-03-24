package com.citc.nce.robot.api.materialSquare.vo.summary;

import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.robot.api.materialSquare.emums.MsAuditStatus;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bydud
 * @since 2024/6/20 15:13
 */
@Data
public class MsAuditPage extends PageParam {
    @ApiModelProperty("(商品)归属用户查询字段")
    private String cspQuery;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("类型")
    private MsType msType;
    @ApiModelProperty("商品状态")
    private MsAuditStatus auditStatus;
}
