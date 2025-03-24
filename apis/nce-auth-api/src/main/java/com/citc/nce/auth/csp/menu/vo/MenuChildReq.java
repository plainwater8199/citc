package com.citc.nce.auth.csp.menu.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.vo
 * @Author: litao
 * @CreateTime: 2023-02-16  14:18
 
 * @Version: 1.0
 */
@Data
public class MenuChildReq implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("父菜单id")
    private Long parentId;

    @ApiModelProperty("菜单编码")
    private String menuCode;

    @ApiModelProperty("按钮id")
    private String buttonId;

    @NotEmpty(message = "按钮类型不能为空")
    @ApiModelProperty(value = "按钮类型", required = true)
    private String buttonType;

    @ApiModelProperty(value = "按钮说明")
    private String buttonDesc;

    @NotEmpty(message = "按钮内容不能为空")
    @ApiModelProperty(value = "按钮内容", required = true)
    private String buttonContent;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    @ApiModelProperty("选项分组")
    private List<Integer> option;
}
