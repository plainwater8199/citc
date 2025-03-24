package com.citc.nce.authcenter.auth.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.vo
 * @Author: litao
 * @CreateTime: 2023-02-16  14:18
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class MenuChildResp {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("父菜单id")
    private Long parentId;

    @ApiModelProperty("菜单编码")
    private String menuCode;

    @ApiModelProperty("按钮id")
    private String buttonId;

    @ApiModelProperty("按钮类型")
    private String buttonType;

    @ApiModelProperty("按钮说明")
    private String buttonDesc;

    @ApiModelProperty("按钮内容")
    private String buttonContent;

    @ApiModelProperty("排序")
    private Integer sort;
}
