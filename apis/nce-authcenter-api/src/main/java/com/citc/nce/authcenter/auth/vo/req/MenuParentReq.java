package com.citc.nce.authcenter.auth.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.vo
 * @Author: litao
 * @CreateTime: 2023-02-16  14:58
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class MenuParentReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("菜单id")
    private Long menuId;

    @ApiModelProperty("菜单编码")
    private String menuCode;

    @NotNull(message = "菜单类型不能为空")
    @ApiModelProperty(value = "菜单类型（0：父菜单 1：功能选项）", required = true)
    private Integer menuType;

    @NotEmpty(message = "菜单内容不能为空")
    @ApiModelProperty(value = "菜单内容", required = true)
    private String menuContent;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty("子菜单")
    private List<MenuChildReq> menuChildReqList;
}
