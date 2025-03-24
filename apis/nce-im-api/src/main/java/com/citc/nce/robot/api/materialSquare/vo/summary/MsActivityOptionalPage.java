package com.citc.nce.robot.api.materialSquare.vo.summary;

import com.citc.nce.common.core.pojo.PageParam;
import com.citc.nce.robot.api.materialSquare.emums.MsSource;
import com.citc.nce.robot.api.materialSquare.emums.MsType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author bydud
 * @since 2024/6/19 11:59
 */
@Data
public class MsActivityOptionalPage extends PageParam {
    @ApiModelProperty("类型")
    private MsType msTypes;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("来源")
    private MsSource msSource;
    @ApiModelProperty("发布者id")
    private String creator;

}
