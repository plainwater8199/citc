package com.citc.nce.robot.vo.ButtonDetail;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ButtonDetailInfo {
    @ApiModelProperty("按钮信息")
    private InputInfo inputInfo;
    @ApiModelProperty("选项分组")
    private List<Integer> option;
    @ApiModelProperty("按钮类型")
    private Integer type;
    @ApiModelProperty("组件信息")
    private String businessId;
}
