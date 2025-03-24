package com.citc.nce.authcenter.tenantdata.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.citc.nce.mybatis.core.entity.BaseDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsPackage: com.citc.nce.auth.csp.menu.entity
 * @Author: litao
 * @CreateTime: 2023-02-16  14:48
 
 * @Version: 1.0
 */
@Data
@TableName("chatbot_manage_menu_parent")
public class MenuParentDo extends BaseDo<MenuParentDo> implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("菜单id")
    private Long menuId;

    @ApiModelProperty("菜单编码")
    private String menuCode;

    @ApiModelProperty("菜单类型(0:父菜单 1:功能选项)")
    private Integer menuType;

    @ApiModelProperty("菜单内容")
    private String menuContent;

    @ApiModelProperty("排序")
    private Integer sort;

    private String creatorOld;

    private String updaterOld;
}
