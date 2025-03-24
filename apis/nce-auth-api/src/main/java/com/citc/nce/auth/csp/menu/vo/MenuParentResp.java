package com.citc.nce.auth.csp.menu.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.vo
 * @Author: litao
 * @CreateTime: 2023-02-16  14:17
 
 * @Version: 1.0
 */
@Data
public class MenuParentResp {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("菜单id")
    private Long menuId;

    @ApiModelProperty("菜单类型(0:父菜单 1:功能选项)")
    private Integer menuType;

    @ApiModelProperty("菜单编码")
    private String menuCode;

    @ApiModelProperty("菜单内容")
    private String menuContent;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("子菜单")
    private List<MenuChildResp> menuChildRespList;
}
