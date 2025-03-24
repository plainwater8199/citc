package com.citc.nce.auth.csp.menu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.entity
 * @Author: litao
 * @CreateTime: 2023-02-16  14:50

 * @Version: 1.0
 */
@Data
@TableName("chatbot_manage_menu_child")
public class MenuChildDo extends BaseDo<MenuChildDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("菜单id")
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

    @ApiModelProperty("选项分组")
    private String optionList;
}
