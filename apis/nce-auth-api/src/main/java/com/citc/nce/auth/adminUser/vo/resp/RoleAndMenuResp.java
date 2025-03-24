package com.citc.nce.auth.adminUser.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author: ylzouf
 * @Contact: ylzouf
 * @Date: 2022/9/23 11:09
 * @Version 1.0
 * @Description:
 */
@Data
@Accessors(chain = true)
public class RoleAndMenuResp {
    private static final Boolean IS_CHOOSE = false;
    @ApiModelProperty(value = "菜单code", dataType = "String")
    private String menuCode;
    @ApiModelProperty(value = "菜单name", dataType = "String")
    private String menuName;
    @ApiModelProperty(value = "菜单url", dataType = "String")
    private String menuUrl;
    @ApiModelProperty(value = "父级code", dataType = "String")
    private String menuParentCode;
    @ApiModelProperty(value = "层级", dataType = "Integer")
    private Integer level;
    @ApiModelProperty(value = "排序", dataType = "Integer")
    private Integer sort;
    @ApiModelProperty(value = "1按钮2菜单", dataType = "Integer")
    private Integer type;
    @ApiModelProperty(value = "0展示1隐藏", dataType = "Integer")
    private Integer hidden;
    @ApiModelProperty(value = "重定向url")
    private String redirect;
    @ApiModelProperty(value = "是否选中", dataType = "Boolean")
    private Boolean choose = IS_CHOOSE;
    private List<RoleAndMenuResp> children;
}
