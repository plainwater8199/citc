package com.citc.nce.auth.csp.menu.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.vo
 * @Author: litao
 * @CreateTime: 2023-02-21  14:45

 * @Version: 1.0
 */
@Data
public class MenuResp {

    @ApiModelProperty("菜单状态 0：待审核，1：审核通过，2：审核不通过")
    private Integer menuStatus;

    @ApiModelProperty("当前菜单")
    private List<MenuParentResp> menuParentRespList;

    @ApiModelProperty("提交记录")
    private List<MenuRecordResp> menuRecordRespList;

    private Integer version;

    private Boolean isShowReduction;
}
